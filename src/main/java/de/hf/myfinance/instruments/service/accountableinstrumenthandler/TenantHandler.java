package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.service.InstrumentFactory;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithGraphAndFactory;
import de.hf.myfinance.restmodel.InstrumentType;

import java.util.ArrayList;
import java.util.List;

public class TenantHandler extends AbsAccountableInstrumentHandler {
    private InstrumentFactory instrumentFactory;

    private static final String DEFAULT_ACCPF_PREFIX = "accPf_";
    private static final String DEFAULT_BUDGETPF_PREFIX = "bgtPf_";
    private static final String DEFAULT_BUDGETGROUP_PREFIX = "bgtGrp_";


    public TenantHandler(InstrumentEnvironmentWithGraphAndFactory instrumentEnvironment, String description, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, null, businesskey, isNewInstrument);
        this.instrumentFactory = instrumentEnvironment.getInstrumentFactory();
    }

    protected void updateParent() {
        setParent(instrumentId, false);
    } 

    @Override
    protected void saveNewInstrument(InstrumentEntity instrumentEntity) {
        super.saveNewInstrument(instrumentEntity);

        var budgetPortfolioHandler = instrumentFactory.getInstrumentHandlerForNewInstrument(InstrumentType.BUDGETPORTFOLIO, DEFAULT_BUDGETPF_PREFIX+instrumentEntity.getDescription(), instrumentId);
        budgetPortfolioHandler.setTreeLastChanged(ts);
        budgetPortfolioHandler.save();
        var budgetGroupHandler = instrumentFactory.getInstrumentHandlerForNewInstrument(InstrumentType.BUDGETGROUP, DEFAULT_BUDGETGROUP_PREFIX+instrumentEntity.getDescription(), budgetPortfolioHandler.getInstrumentId());
        budgetGroupHandler.setTreeLastChanged(ts);
        budgetGroupHandler.save();

        var accPortfolioHandler = instrumentFactory.getInstrumentHandlerForNewInstrument(InstrumentType.ACCOUNTPORTFOLIO, DEFAULT_ACCPF_PREFIX+instrumentEntity.getDescription(), instrumentId);
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
    protected InstrumentEntity createDomainObject() {
        return new InstrumentEntity(InstrumentType.TENANT, description, true, ts);
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