package de.hf.myfinance.instruments.service.securityhandler;

import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.service.AbsInstrumentHandler;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.AdditionalMaps;
import de.hf.myfinance.restmodel.AdditionalProperties;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class EquityHandler extends AbsInstrumentHandler {
    public EquityHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
    }

    @Override
    protected Instrument createDomainObject() {
        return new Instrument(businesskey, requestedInstrument.getDescription(), InstrumentType.EQUITY, true, ts);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.EQUITY;
    }

    @Override
    protected Mono<Instrument> setAdditionalValues(Instrument instrument) {

        if(requestedInstrument.getAdditionalMaps().containsKey(AdditionalMaps.EQUITYSYMBOLS)) {
            var symbols = requestedInstrument.getAdditionalMaps().get(AdditionalMaps.EQUITYSYMBOLS);

            var additionalMap = new HashMap<AdditionalMaps, Map<String,String>>();
            additionalMap.put(AdditionalMaps.EQUITYSYMBOLS, symbols);
            instrument.setAdditionalMaps(additionalMap);
        }

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

    @Override
    protected String initBusinesskey() {
        if(requestedInstrument.getAdditionalProperties()==null
                || requestedInstrument.getAdditionalProperties().get(AdditionalProperties.ISIN)==null
                || requestedInstrument.getAdditionalProperties().get(AdditionalProperties.ISIN).isEmpty()){
            throw new MFException(MFMsgKey.NO_VALID_INSTRUMENT, "wether this businesskey nor the isin is defined for the instrument");
        }
        var isin = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.ISIN);
        if(isin.length()!=12) {
            throw new MFException(MFMsgKey.NO_VALID_INSTRUMENT, "isin has the wrong size:"+ isin);
        }
        return isin.toUpperCase();
    }
}
