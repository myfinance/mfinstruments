package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import java.util.HashMap;

import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.AdditionalProperties;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import reactor.core.publisher.Mono;


public class DeprecationObjectHandler  extends AbsAccountHandler {

    public DeprecationObjectHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.DEPRECATIONOBJECT;
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

            if(requestedInstrument.getAdditionalProperties().get(AdditionalProperties.VALUEBUDGETID)!=null
            && !requestedInstrument.getAdditionalProperties().get(AdditionalProperties.VALUEBUDGETID).isEmpty()) {
                var value = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.VALUEBUDGETID);
                properties.put(AdditionalProperties.VALUEBUDGETID, value);
            } else {
                return auditService.handleMonoError("for DeprecationObjects it is not allowed to have no Valuebudget", AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT).cast(Instrument.class);
            }

            if(requestedInstrument.getAdditionalProperties().get(AdditionalProperties.ACQUISITIONDATE)!=null
            && !requestedInstrument.getAdditionalProperties().get(AdditionalProperties.ACQUISITIONDATE).isEmpty()) {
                var value = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.ACQUISITIONDATE);
                properties.put(AdditionalProperties.ACQUISITIONDATE, value);
            } else {
                return auditService.handleMonoError("for DeprecationObjects it is not allowed to have no ACQUISITIONDATE", AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT).cast(Instrument.class);
            }

            if(requestedInstrument.getAdditionalProperties().get(AdditionalProperties.ACQUISITIONVALUE)!=null
            && !requestedInstrument.getAdditionalProperties().get(AdditionalProperties.ACQUISITIONVALUE).isEmpty()) {
                var value = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.ACQUISITIONVALUE);
                properties.put(AdditionalProperties.ACQUISITIONVALUE, value);
            } else {
                return auditService.handleMonoError("for DeprecationObjects it is not allowed to have no ACQUISITIONVALUE", AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT).cast(Instrument.class);
            }

            if(requestedInstrument.getAdditionalProperties().get(AdditionalProperties.MATURITYDATE)!=null
            && !requestedInstrument.getAdditionalProperties().get(AdditionalProperties.MATURITYDATE).isEmpty()) {
                var value = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.MATURITYDATE);
                properties.put(AdditionalProperties.MATURITYDATE, value);
            } else {
                return auditService.handleMonoError("for DeprecationObjects it is not allowed to have no MATURITYDATE", AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT).cast(Instrument.class);
            }

            instrument.setAdditionalProperties(properties);
        } else {
            return auditService.handleMonoError("for DeprecationObjects it is not allowed to have no additional properties", AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT).cast(Instrument.class);
        }

        return Mono.just(instrument);
    }
}