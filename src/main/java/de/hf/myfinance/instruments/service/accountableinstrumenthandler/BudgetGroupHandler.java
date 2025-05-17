package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import java.util.HashMap;

import de.hf.myfinance.instruments.service.InstrumentFactory;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithFactory;
import de.hf.myfinance.restmodel.AdditionalProperties;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import reactor.core.publisher.Mono;


public class BudgetGroupHandler extends AbsAccountableInstrumentHandler {
    private  final InstrumentFactory instrumentFactory;
    private static final String DEFAULT_INCOMEBUDGET_PREFIX = "incomeBgt_";

    public BudgetGroupHandler(InstrumentEnvironmentWithFactory instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
        this.instrumentFactory = instrumentEnvironment.getInstrumentFactory();
    }

    @Override
    protected Mono<String> postApproveAction(Instrument instrument){
        var budgetHandler = getBudgetHandler(instrument.getTenantBusinesskey());
        return budgetHandler.save();
    }

    private AccountableInstrumentHandler getBudgetHandler(String tenantKey) {
        var budget = new Instrument(DEFAULT_INCOMEBUDGET_PREFIX+requestedInstrument.getDescription(), InstrumentType.BUDGET);
        budget.setParentBusinesskey(businesskey);
        var budgetHandler = (AccountableInstrumentHandler)instrumentFactory.getInstrumentHandler(budget);
        budgetHandler.setTreeLastChanged(ts);
        budgetHandler.setIsSimpleValidation(true);

        if(isSimpleValidation) {
            // block is ok here. Due to the simplevalidate the tenantbusinesskey is not read from the db but create with just
            budgetHandler.setTenant(tenantKey);
        }
        return budgetHandler;
    }

    @Override
    protected Instrument createDomainObject() {
        return new Instrument(businesskey, requestedInstrument.getDescription(), InstrumentType.BUDGETGROUP, true, ts);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.BUDGETGROUP;
    }


    @Override
    protected InstrumentType getParentType() {
        return InstrumentType.BUDGETPORTFOLIO;
    }

    @Override
    protected Mono<Instrument> setAdditionalValues(Instrument instrument) {
        var properties = new HashMap<AdditionalProperties, String>();
        if(requestedInstrument.getAdditionalProperties()!=null
                && requestedInstrument.getAdditionalProperties().get(AdditionalProperties.LINKEDINSTRUMENTID)!=null
                && !requestedInstrument.getAdditionalProperties().get(AdditionalProperties.LINKEDINSTRUMENTID).isEmpty()){
            var linkedInstrumentId = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.LINKEDINSTRUMENTID);
            properties.put(AdditionalProperties.LINKEDINSTRUMENTID, linkedInstrumentId);
            
        } 
        var budgetKey = getBudgetHandler(instrument.getTenantBusinesskey()).initBusinesskey();
        properties.put(AdditionalProperties.INCOMEBUDGETID, budgetKey);
        instrument.setAdditionalProperties(properties);
        return Mono.just(instrument);
    }
} 