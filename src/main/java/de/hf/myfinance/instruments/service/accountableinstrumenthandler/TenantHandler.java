package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.framework.audit.AuditService;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.service.InstrumentFactory;
import de.hf.myfinance.restmodel.InstrumentType;

import java.util.ArrayList;
import java.util.List;

public class TenantHandler extends AbsAccountableInstrumentHandler {
    private InstrumentFactory instrumentFactory;

    private static final String DEFAULT_ACCPF_PREFIX = "accPf_";
    private static final String DEFAULT_BUDGETPF_PREFIX = "bgtPf_";
    private static final String DEFAULT_BUDGETGROUP_PREFIX = "bgtGrp_";
    

    public TenantHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, InstrumentFactory instrumentFactory, String businesskey) {
        super(instrumentRepository, instrumentGraphRepository, auditService, businesskey);
        this.instrumentFactory = instrumentFactory;
    }

    public TenantHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, InstrumentFactory instrumentFactory, String description, String businesskey) {
        super(instrumentRepository, instrumentGraphRepository, auditService, description, null, businesskey);
        this.instrumentFactory = instrumentFactory;
    }

    protected void updateParent() {
        setParent(instrumentId, false);
    } 

    @Override
    protected void saveNewInstrument() {
        super.saveNewInstrument();

        var budgetPortfolioHandler = instrumentFactory.getInstrumentHandler(InstrumentType.BUDGETPORTFOLIO, DEFAULT_BUDGETPF_PREFIX+domainObject.getDescription(), instrumentId, null);
        budgetPortfolioHandler.setTreeLastChanged(ts);
        budgetPortfolioHandler.save();
        var budgetGroupHandler = instrumentFactory.getInstrumentHandler(InstrumentType.BUDGETGROUP, DEFAULT_BUDGETGROUP_PREFIX+domainObject.getDescription(), budgetPortfolioHandler.getInstrumentId(), null);
        budgetGroupHandler.setTreeLastChanged(ts);
        budgetGroupHandler.save();

        var accPortfolioHandler = instrumentFactory.getInstrumentHandler(InstrumentType.ACCOUNTPORTFOLIO, DEFAULT_ACCPF_PREFIX+domainObject.getDescription(), instrumentId, null);
        accPortfolioHandler.setTreeLastChanged(ts);
        accPortfolioHandler.save();
    }

    public List<InstrumentEntity> listInstruments() {
        return instrumentGraphHandler.getAllInstrumentChilds(instrumentId);
    }

    public List<InstrumentEntity> listInstruments(boolean onlyActive) {
        return instrumentGraphHandler.getAllInstrumentChilds(instrumentId, onlyActive);
    }

    public List<InstrumentEntity> listInstruments(InstrumentType instrumentType, boolean onlyActive) {
        return instrumentGraphHandler.getAllInstrumentChilds(instrumentId, instrumentType, onlyActive);
    }

    public InstrumentEntity getAccountPortfolio() {
        return instrumentGraphHandler.getFirstLevelChildsPerTypeFirstmatch(instrumentId, InstrumentType.ACCOUNTPORTFOLIO);
    }

    public InstrumentEntity getBudgetPortfolio() {
        return instrumentGraphHandler.getFirstLevelChildsPerTypeFirstmatch(instrumentId, InstrumentType.BUDGETPORTFOLIO);
    }

    public List<InstrumentEntity> getAccounts() {
        InstrumentEntity accPF = getAccountPortfolio();
        if(accPF==null) {
            return new ArrayList<>();
        }
        return instrumentGraphHandler.getInstrumentFirstLevelChilds(accPF.getInstrumentid());
    }

    @Override
    protected void createDomainObject() {
        domainObject = new InstrumentEntity(InstrumentType.TENANT, description, true, ts);
    }

    @Override
    protected void setDomainObjectName() {
        domainObjectName = "Tenant";
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.TENANT;
    }

    @Override
    protected void validateParent() {
        //Tenants have no parents
    }
}