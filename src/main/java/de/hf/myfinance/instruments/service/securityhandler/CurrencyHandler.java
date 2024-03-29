package de.hf.myfinance.instruments.service.securityhandler;

import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.service.AbsInstrumentHandler;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.AdditionalProperties;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import reactor.core.publisher.Mono;

import java.util.HashMap;

public class CurrencyHandler extends AbsInstrumentHandler {
    public CurrencyHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
    }

    @Override
    protected Instrument createDomainObject() {
        return new Instrument(businesskey, requestedInstrument.getDescription(), InstrumentType.CURRENCY, true, ts);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.CURRENCY;
    }

    @Override
    protected Mono<Instrument> setAdditionalValues(Instrument instrument) {
        if(requestedInstrument.getAdditionalProperties()!=null
                && requestedInstrument.getAdditionalProperties().get(AdditionalProperties.CURRENCYCODE)!=null
                && !requestedInstrument.getAdditionalProperties().get(AdditionalProperties.CURRENCYCODE).isEmpty()){
            var currencyCode = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.CURRENCYCODE);
            var properties = new HashMap<AdditionalProperties, String>();
            properties.put(AdditionalProperties.CURRENCYCODE, currencyCode.toUpperCase());
            instrument.setAdditionalProperties(properties);
        }


        return Mono.just(instrument);
    }

    @Override
    protected String initBusinesskey() {
        if(requestedInstrument.getAdditionalProperties()==null
                || requestedInstrument.getAdditionalProperties().get(AdditionalProperties.CURRENCYCODE)==null
                || requestedInstrument.getAdditionalProperties().get(AdditionalProperties.CURRENCYCODE).isEmpty()){
            auditService.throwException("wether this businesskey nor the isin is defined for the instrument", AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT);
        }
        var currencyCode = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.CURRENCYCODE);
        if(currencyCode.length()!=3) {
            auditService.throwException("currencycode has the wrong size:"+ currencyCode, AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT);
        }
        return currencyCode.toUpperCase();
    }
}