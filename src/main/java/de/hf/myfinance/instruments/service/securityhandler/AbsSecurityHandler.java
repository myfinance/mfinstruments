package de.hf.myfinance.instruments.service.securityhandler;

import java.util.HashMap;

import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.service.AbsInstrumentHandler;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.AdditionalProperties;
import de.hf.myfinance.restmodel.Instrument;
import reactor.core.publisher.Mono;

public abstract class AbsSecurityHandler extends AbsInstrumentHandler {
    public AbsSecurityHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
    }

    @Override
    protected String initBusinesskey() {
        if(requestedInstrument.getAdditionalProperties()==null
                || requestedInstrument.getAdditionalProperties().get(AdditionalProperties.ISIN)==null
                || requestedInstrument.getAdditionalProperties().get(AdditionalProperties.ISIN).isEmpty()){
            throw new MFException(MFMsgKey.NO_VALID_INSTRUMENT, "wether this businesskey nor the isin is defined for the instrument");
        }
        var isin = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.ISIN);
        if(isin.length()!=12) {
            auditService.throwException("isin has the wrong size:"+ isin, AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT);
        }
        return isin.toUpperCase();
    }

    @Override
    protected Mono<Instrument> setAdditionalValues(Instrument instrument) {
        if(requestedInstrument.getAdditionalProperties()!=null
                && requestedInstrument.getAdditionalProperties().get(AdditionalProperties.ISIN)!=null
                && !requestedInstrument.getAdditionalProperties().get(AdditionalProperties.ISIN).isEmpty()){
            var isin = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.ISIN);
            var properties = new HashMap<AdditionalProperties, String>();
            properties.put(AdditionalProperties.ISIN, isin.toUpperCase());
            instrument.setAdditionalProperties(properties);
        }


        return Mono.just(instrument);
    }
}
