package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.framework.audit.AuditService;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.restmodel.InstrumentType;

public class BudgetPortfolioHandler extends AbsAccountableInstrumentHandler {
    
    public BudgetPortfolioHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, String description, String tenantId, String businesskey) {
        super(instrumentRepository, instrumentGraphRepository, auditService, description, tenantId, businesskey);
    }

    public BudgetPortfolioHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, InstrumentEntity budgetPortfolio) {
        super(instrumentRepository, instrumentGraphRepository, auditService, budgetPortfolio);
    }

    public BudgetPortfolioHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, String instrumentId) {
        super(instrumentRepository, instrumentGraphRepository, auditService, instrumentId);
    }

    @Override
    protected void createDomainObject() {
        domainObject = new InstrumentEntity(InstrumentType.BUDGETPORTFOLIO, description, true, ts);
    }

    @Override
    protected void setDomainObjectName() {
        domainObjectName = "BudgetPortfolio";
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.BUDGETPORTFOLIO;
    }
}