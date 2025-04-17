package de.hf.myfinance.instruments.service.accountableinstrumenthandler;


import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.AdditionalProperties;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import reactor.core.publisher.Mono;

public abstract class AbsAccountHandler extends AbsAccountableInstrumentHandler {

    protected AbsAccountHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
    }

    @Override
    protected InstrumentType getParentType() {
        return InstrumentType.ACCOUNTPORTFOLIO;
    }

    protected Mono<Instrument> validateInstrumentWithValueBudget(Mono<Instrument> instrumentMono){
        if(requestedInstrument.getAdditionalProperties()!=null
                && requestedInstrument.getAdditionalProperties().size()>0
                && requestedInstrument.getAdditionalProperties().get(AdditionalProperties.VALUEBUDGETID)!= null) {

            var budgetId = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.VALUEBUDGETID);
            instrumentMono=Mono.zip(instrumentMono, loadBudget(budgetId), this::validateBudget);

        }else {
            var desc = "NA";
            if(requestedInstrument!=null && requestedInstrument.getDescription()!=null){
                desc = requestedInstrument.getDescription();
            }
            auditService.throwException("Instrument not saved with desc: "+ desc + " has no value Budget", AUDIT_MSG_TYPE, MFMsgKey.WRONG_INSTRUMENTTYPE_EXCEPTION);

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