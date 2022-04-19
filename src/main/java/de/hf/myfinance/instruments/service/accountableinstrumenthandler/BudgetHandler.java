package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.framework.audit.AuditService;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.restmodel.InstrumentType;

public class BudgetHandler extends AbsCashInstrumentHandler {

    public BudgetHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, String description, String budgetGroupId, String businesskey) {
        super(instrumentRepository, instrumentGraphRepository, auditService, description, budgetGroupId, businesskey);
    }

    public BudgetHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, InstrumentEntity budget) {
        super(instrumentRepository, instrumentGraphRepository, auditService, budget);
    }

    public BudgetHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, String instrumentId) {
        super(instrumentRepository, instrumentGraphRepository, auditService, instrumentId);
    }

    @Override
    protected void createDomainObject() {
        domainObject = new InstrumentEntity(InstrumentType.BUDGET, description, true, ts);
        domainObject.setBusinesskey(businesskey);
    }

    @Override
    protected void setDomainObjectName() {
        domainObjectName = "Budget";
    }

    @Override
    protected InstrumentType getParentType() {
        return InstrumentType.BUDGETGROUP;
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.BUDGET;
    }

    @Override
    public void setDescription(String description) {
        super.setDescription(description);
        super.setBusinesskey(description);
    }
}