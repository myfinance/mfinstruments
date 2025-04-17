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
        return validateInstrumentWithValueBudget(super.validateInstrument(instrument));
    }

}