package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import java.util.ArrayList;
import java.util.HashMap;

import de.hf.myfinance.exception.MFMsgKey;
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
            var desc = "NA";
            if(requestedInstrument!=null && requestedInstrument.getDescription()!=null){
                desc = requestedInstrument.getDescription();
            }
            auditService.throwException("wether this businesskey nor the iban is defined for the instrument with desc:"+desc, AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT);
        }
        var keyProperties = new ArrayList<String>();
        if(requestedInstrument.getParentBusinesskey()!=null) {
            keyProperties.add(requestedInstrument.getParentBusinesskey());
        }
        keyProperties.add(iban);
        keyProperties.add(getInstrumentType().getValue().toString());
        return this.generateUUID(keyProperties);
    }

    @Override
    protected Mono<Instrument> checkKeyFields(Instrument validatedInstrument) {
        if(!isNewInstrument
            && requestedInstrument.getAdditionalProperties() != null 
            && requestedInstrument.getAdditionalProperties().get(AdditionalProperties.IBAN) != null && !requestedInstrument.getAdditionalProperties().get(AdditionalProperties.IBAN).isEmpty()){
                var newIban = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.IBAN);
                if(!newIban.equals(validatedInstrument.getAdditionalProperties().get(AdditionalProperties.IBAN))){
                    return auditService.handleMonoError("you can not change the Iban because it is part of the key", AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT).cast(Instrument.class);
                }
        }
        return Mono.just(validatedInstrument);
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
            else {
                return auditService.handleMonoError("for Giro it is not allowed to have no IBAN", AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT).cast(Instrument.class);
            }

            instrument.setAdditionalProperties(properties);
        } 

        return Mono.just(instrument);
    }
}