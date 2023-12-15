package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.AdditionalProperties;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;

public class GiroHandler extends AbsCashInstrumentHandler {

    public GiroHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
    }

    @Override
    protected Instrument createDomainObject() {
        return new Instrument(businesskey, requestedInstrument.getDescription(), InstrumentType.GIRO, true, ts);
    }

    @Override
    protected InstrumentType getParentType() {
        return InstrumentType.ACCOUNTPORTFOLIO;
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
}