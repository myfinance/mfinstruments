package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithGraph;
import de.hf.myfinance.restmodel.InstrumentType;

public class AccountPortfolioHandler extends AbsAccountableInstrumentHandler {

    public AccountPortfolioHandler(InstrumentEnvironmentWithGraph instrumentEnvironment, String description, String tenantId, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, tenantId, businesskey, isNewInstrument);
    }

    @Override
    protected InstrumentEntity createDomainObject() {
        return new InstrumentEntity(InstrumentType.ACCOUNTPORTFOLIO, description, true, ts);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.ACCOUNTPORTFOLIO;
    }
}