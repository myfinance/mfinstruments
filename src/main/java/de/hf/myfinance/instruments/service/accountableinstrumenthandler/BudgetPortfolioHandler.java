package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithGraph;
import de.hf.myfinance.restmodel.InstrumentType;

public class BudgetPortfolioHandler extends AbsAccountableInstrumentHandler {
    
    public BudgetPortfolioHandler(InstrumentEnvironmentWithGraph instrumentEnvironment, String description, String tenantId, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, tenantId, businesskey, isNewInstrument);
    }

    @Override
    protected InstrumentEntity createDomainObject() {
        return new InstrumentEntity(InstrumentType.BUDGETPORTFOLIO, description, true, ts);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.BUDGETPORTFOLIO;
    }
}