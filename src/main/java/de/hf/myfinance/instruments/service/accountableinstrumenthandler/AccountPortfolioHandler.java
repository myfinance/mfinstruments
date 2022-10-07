package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;

public class AccountPortfolioHandler extends AbsAccountableInstrumentHandler {

    public AccountPortfolioHandler(InstrumentEnvironment instrumentEnvironment, String description, String tenantId, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, tenantId, businesskey, isNewInstrument);
    }

    @Override
    protected Instrument createDomainObject() {
        return new Instrument(businesskey, description, InstrumentType.ACCOUNTPORTFOLIO, true, ts);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.ACCOUNTPORTFOLIO;
    }
}