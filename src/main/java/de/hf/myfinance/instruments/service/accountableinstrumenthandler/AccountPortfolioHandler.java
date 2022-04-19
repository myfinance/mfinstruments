package de.hf.myfinance.instruments.service.accountableinstrumenthandler;


import de.hf.framework.audit.AuditService;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.restmodel.InstrumentType;

public class AccountPortfolioHandler extends AbsAccountableInstrumentHandler {

    public AccountPortfolioHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, String description,
                                   String tenantId, String businesskey) {
        super(instrumentRepository, instrumentGraphRepository, auditService, description, tenantId, businesskey);
    }

    public AccountPortfolioHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, InstrumentEntity accountPortfolio) {
        super(instrumentRepository, instrumentGraphRepository, auditService, accountPortfolio);
    }

    public AccountPortfolioHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, String instrumentId) {
        super(instrumentRepository, instrumentGraphRepository, auditService, instrumentId);
    }

    @Override
    protected void createDomainObject() {
        domainObject = new InstrumentEntity(InstrumentType.ACCOUNTPORTFOLIO, description, true, ts);
    }

    @Override
    protected void setDomainObjectName() {
        domainObjectName = "AccountPortfolio";
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.ACCOUNTPORTFOLIO;
    }
}