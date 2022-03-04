package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.framework.audit.AuditService;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.service.InstrumentFactory;
import de.hf.myfinance.restmodel.InstrumentType;
import de.hf.myfinance.restmodel.Tenant;

import java.util.ArrayList;
import java.util.List;

public class TenantHandler extends AbsAccountableInstrumentHandler {
    private InstrumentFactory instrumentFactory;

    private static final String DEFAULT_ACCPF_PREFIX = "accountPf_";
    private static final String DEFAULT_BUDGETPF_PREFIX = "budgetPf_";
    private static final String DEFAULT_BUDGETGROUP_PREFIX = "budgetGroup_";
    

    public TenantHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, InstrumentFactory instrumentFactory, int tenantId) {
        super(instrumentRepository, instrumentGraphRepository, auditService, tenantId);
        this.instrumentFactory = instrumentFactory;
    }

    public TenantHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, InstrumentFactory instrumentFactory, String description) {
        super(instrumentRepository, instrumentGraphRepository, auditService, description, -1, description);
        this.instrumentFactory = instrumentFactory;
    }

    public TenantHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, InstrumentFactory instrumentFactory, InstrumentEntity tenant) {
        super(instrumentRepository, instrumentGraphRepository, auditService, tenant);
        this.instrumentFactory = instrumentFactory;
    }

    protected void updateParent() {
        setParent(instrumentId, false);
    } 

    @Override
    protected void saveNewInstrument() {
        if(this.businesskey.length()>6) {
            this.businesskey = this.businesskey.substring(0, 6);
            domainObject.setBusinesskey(this.businesskey);
        }
        super.saveNewInstrument();

        var budgetPortfolioHandler = instrumentFactory.getInstrumentHandler(InstrumentType.BUDGETPORTFOLIO, DEFAULT_BUDGETPF_PREFIX+domainObject.getDescription(), instrumentId, DEFAULT_BUDGETPF_PREFIX+domainObject.getBusinesskey());
        budgetPortfolioHandler.setTreeLastChanged(ts);
        budgetPortfolioHandler.save();
        var budgetGroupHandler = instrumentFactory.getInstrumentHandler(InstrumentType.BUDGETGROUP, DEFAULT_BUDGETGROUP_PREFIX+domainObject.getDescription(), budgetPortfolioHandler.getInstrumentId(), DEFAULT_BUDGETGROUP_PREFIX+domainObject.getBusinesskey());
        budgetGroupHandler.setTreeLastChanged(ts);
        budgetGroupHandler.save();

        var accPortfolioHandler = instrumentFactory.getInstrumentHandler(InstrumentType.ACCOUNTPORTFOLIO, DEFAULT_ACCPF_PREFIX+domainObject.getDescription(), instrumentId, DEFAULT_ACCPF_PREFIX+domainObject.getBusinesskey());
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
    protected void updateInstrument() {
        super.updateInstrument();
        List<InstrumentEntity> instruments = instrumentGraphHandler.getAllInstrumentChilds(instrumentId);
        renameDefaultTenantChild(instrumentId, description, oldDesc, DEFAULT_BUDGETPF_PREFIX, instruments);
        renameDefaultTenantChild(instrumentId, description, oldDesc, DEFAULT_BUDGETGROUP_PREFIX, instruments);
        renameDefaultTenantChild(instrumentId, description, oldDesc, DEFAULT_ACCPF_PREFIX, instruments); 
    }

    private void renameDefaultTenantChild(int instrumentId, String newDesc, String oldDesc, String defaultDescPrefix, List<InstrumentEntity> instruments) {
        //look by description for default instruments of the tenant to rename
        instruments.stream().filter(i->i.getDescription().equals(defaultDescPrefix+oldDesc)).forEach(i->{
            var handler = instrumentFactory.getInstrumentHandler(i.getInstrumentid());
            handler.setDescription(newDesc);
            handler.setActive(true);
            handler.save();
        });
    }

    @Override
    protected void validateParent() {
        //Tenants have no parents
    }
}