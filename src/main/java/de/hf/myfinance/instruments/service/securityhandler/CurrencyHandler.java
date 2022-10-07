package de.hf.myfinance.instruments.service.securityhandler;

import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;

public class CurrencyHandler extends SecurityHandler {
    public CurrencyHandler(InstrumentEnvironment instrumentEnvironment, String description, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, businesskey, isNewInstrument);
    }

    @Override
    protected Instrument createDomainObject() {
        return new Instrument(businesskey, description, InstrumentType.CURRENCY, true, ts);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.CURRENCY;
    }
}