package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;

public class AccountPortfolioHandler extends AbsAccountableInstrumentHandler {

    public AccountPortfolioHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
    }

    @Override
    protected Instrument createDomainObject() {
        return new Instrument(businesskey, requestedInstrument.getDescription(), InstrumentType.ACCOUNTPORTFOLIO, true, ts);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.ACCOUNTPORTFOLIO;
    }
}