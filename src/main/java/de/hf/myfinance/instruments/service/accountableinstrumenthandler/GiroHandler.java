package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import java.util.HashMap;

import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.AdditionalProperties;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import reactor.core.publisher.Mono;

public class GiroHandler extends AbsAccountHandler {

    public GiroHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
    }



    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.GIRO;
    }

    @Override
    protected String initBusinesskey() {
        var iban = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.IBAN);
        if(iban==null || iban.isEmpty()){
            return super.initBusinesskey();
        }
        return iban;
    }

   @Override
    protected Mono<Instrument> setAdditionalValues(Instrument instrument) {

        if(requestedInstrument.getAdditionalProperties()!=null){

            var properties = new HashMap<AdditionalProperties, String>();

            if(requestedInstrument.getAdditionalProperties().get(AdditionalProperties.IBAN)!=null
            && !requestedInstrument.getAdditionalProperties().get(AdditionalProperties.IBAN).isEmpty()) {
                var value = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.IBAN);
                properties.put(AdditionalProperties.IBAN, value);
            } 

            instrument.setAdditionalProperties(properties);
        } 

        return Mono.just(instrument);
    }
}