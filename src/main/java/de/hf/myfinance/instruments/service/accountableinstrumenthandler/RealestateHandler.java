package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import java.util.HashMap;
import java.util.Map;

import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.service.InstrumentFactory;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithFactory;
import de.hf.myfinance.restmodel.AdditionalMaps;
import de.hf.myfinance.restmodel.AdditionalProperties;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import de.hf.myfinance.restmodel.LiquidityType;
import reactor.core.publisher.Mono;

public class RealestateHandler  extends AbsAccountHandler {

    private static final String DEFAULT_BUDGETGROUP_PREFIX = "bgtGrp_";
    private InstrumentFactory instrumentFactory;

    public RealestateHandler(InstrumentEnvironmentWithFactory instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
        this.instrumentFactory = instrumentEnvironment.getInstrumentFactory();
    }

    @Override
    protected InstrumentType getParentType() {
        return InstrumentType.ACCOUNTPORTFOLIO;
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.REALESTATE;
    }

    @Override
    protected Mono<String> postApproveAction(Instrument instrument){
        return getTenant().flatMap(this::getBudgetPf).flatMap(this::createBudgetGroup);

    }

    protected Mono<String> createBudgetGroup(Instrument budgetPf) {
        var budgetGroup = new Instrument(DEFAULT_BUDGETGROUP_PREFIX+requestedInstrument.getDescription(), InstrumentType.BUDGETGROUP);
        
        budgetGroup.setParentBusinesskey(budgetPf.getBusinesskey());
        var properties = new HashMap<AdditionalProperties,String>();
        properties.put(AdditionalProperties.LINKEDINSTRUMENTID, businesskey);
        budgetGroup.setAdditionalProperties(properties);
        budgetGroup.setTenantBusinesskey(budgetPf.getTenantBusinesskey());

        var budgetGroupHandler = (AccountableInstrumentHandler)instrumentFactory.getInstrumentHandler(budgetGroup);
        budgetGroupHandler.setTreeLastChanged(ts);
        budgetGroupHandler.setIsSimpleValidation(true);
        
        budgetGroupHandler.setTenant(budgetPf.getTenantBusinesskey());
        return budgetGroupHandler.save();
    }

    @Override
    protected Mono<Instrument> setAdditionalValues(Instrument instrument) {

        if(requestedInstrument.getAdditionalMaps().containsKey(AdditionalMaps.YIELDGOAL)
                && requestedInstrument.getAdditionalMaps().containsKey(AdditionalMaps.REALESTATEPROFITS)) {
            var yieldgoals = requestedInstrument.getAdditionalMaps().get(AdditionalMaps.YIELDGOAL);
            var profits = requestedInstrument.getAdditionalMaps().get(AdditionalMaps.REALESTATEPROFITS);

            var additionalMap = new HashMap<AdditionalMaps, Map<String,String>>();
            additionalMap.put(AdditionalMaps.YIELDGOAL, yieldgoals);
            additionalMap.put(AdditionalMaps.REALESTATEPROFITS, profits);
            instrument.setAdditionalMaps(additionalMap);
        }

        if(requestedInstrument.getAdditionalProperties()!=null
                && requestedInstrument.getAdditionalProperties().get(AdditionalProperties.VALUEBUDGETID)!=null
                && !requestedInstrument.getAdditionalProperties().get(AdditionalProperties.VALUEBUDGETID).isEmpty()){
            var valueBudgetId = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.VALUEBUDGETID);
            var properties = new HashMap<AdditionalProperties, String>();
            properties.put(AdditionalProperties.VALUEBUDGETID, valueBudgetId);
            instrument.setAdditionalProperties(properties);
        } else {
            return auditService.handleMonoError("for realestates it is not allowed to have no Valuebudget", AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT).cast(Instrument.class);
        }

        return Mono.just(instrument);
    }
    @Override
    protected Mono<Instrument> setLiquidityType(Instrument instrument) {
        instrument.setLiquidityType(LiquidityType.LONGTERM);
        return Mono.just(instrument);
    }
}