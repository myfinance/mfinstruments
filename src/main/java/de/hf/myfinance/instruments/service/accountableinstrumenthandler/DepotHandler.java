package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import java.util.HashMap;

import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.AdditionalProperties;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import de.hf.myfinance.restmodel.LiquidityType;
import reactor.core.publisher.Mono;

public class DepotHandler extends AbsAccountHandler {

    public DepotHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
    }

    @Override
    protected Instrument createDomainObject() {
        return new Instrument(businesskey, requestedInstrument.getDescription(), InstrumentType.DEPOT, true, ts);
    }

    @Override
    protected InstrumentType getParentType() {
        return InstrumentType.ACCOUNTPORTFOLIO;
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.DEPOT;
    }

    @Override
    protected Mono<Instrument> setLiquidityType(Instrument instrument) {
        instrument.setLiquidityType(LiquidityType.MIDTERM);
        return Mono.just(instrument);
    }

    @Override
    protected Mono<Instrument> setAdditionalValues(Instrument instrument) {

        if(requestedInstrument.getAdditionalProperties()!=null
                && requestedInstrument.getAdditionalProperties().get(AdditionalProperties.VALUEBUDGETID)!=null
                && !requestedInstrument.getAdditionalProperties().get(AdditionalProperties.VALUEBUDGETID).isEmpty()){
            var valueBudgetId = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.VALUEBUDGETID);
            var properties = new HashMap<AdditionalProperties, String>();
            properties.put(AdditionalProperties.VALUEBUDGETID, valueBudgetId);
            instrument.setAdditionalProperties(properties);
        }


        return Mono.just(instrument);
    }

    @Override
    protected Mono<Instrument> validateInstrument(Instrument instrument){
        var instrumentMono = super.validateInstrument(instrument);
        if(requestedInstrument.getAdditionalProperties()!=null
                && requestedInstrument.getAdditionalProperties().size()>0
                && requestedInstrument.getAdditionalProperties().get(AdditionalProperties.VALUEBUDGETID)!= null) {

            var budgetId = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.VALUEBUDGETID);
            instrumentMono=Mono.zip(instrumentMono, loadBudget(budgetId), this::validateBudget);

        }else {
            auditService.throwException("Instrument not saved with Id "+ instrument.getBusinesskey() + " has no value Budget", AUDIT_MSG_TYPE, MFMsgKey.WRONG_INSTRUMENTTYPE_EXCEPTION);

        }
        return instrumentMono;
    }

    protected Instrument validateBudget(Instrument instrument, Instrument budget) {
        if(budget.getInstrumentType()!=InstrumentType.BUDGET){
            auditService.throwException("Instrument not saved with Id "+ instrument.getBusinesskey() + ", valueBudget is not a Budget", AUDIT_MSG_TYPE, MFMsgKey.WRONG_INSTRUMENTTYPE_EXCEPTION);
        }
        return instrument;
    }

    private Mono<Instrument> loadBudget(String Businesskey) {
        return dataReader.findByBusinesskey(Businesskey)
                .switchIfEmpty(auditService.handleMonoError("Instrument not saved: budget unknown:"+ Businesskey, AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT).cast(Instrument.class));
    }
}