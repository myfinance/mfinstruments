package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.framework.audit.AuditService;
import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.entities.InstrumentPropertiesEntity;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.service.InstrumentFactory;
import de.hf.myfinance.restmodel.InstrumentPropertyType;
import de.hf.myfinance.restmodel.InstrumentType;

import java.util.Optional;

public class BudgetGroupHandler extends AbsAccountableInstrumentHandler {
    private  final InstrumentFactory instrumentFactory;
    private static final String DEFAULT_INCOMEBUDGET_PREFIX = "incomeBudget_";

    public BudgetGroupHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, InstrumentFactory instrumentFactory, String budgetGroupId) {
        super(instrumentRepository, instrumentGraphRepository, auditService, budgetGroupId);
        this.instrumentFactory = instrumentFactory;
    }

    public BudgetGroupHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, InstrumentFactory instrumentFactory, InstrumentEntity budgetGroup) {
        super(instrumentRepository, instrumentGraphRepository, auditService, budgetGroup);
        this.instrumentFactory = instrumentFactory;
    }

    public BudgetGroupHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, InstrumentFactory instrumentFactory, String description, String budgetPFId, String businesskey) {
        super(instrumentRepository, instrumentGraphRepository, auditService, description, budgetPFId, businesskey);
        this.instrumentFactory = instrumentFactory;
    }

    public InstrumentEntity getIncomeBudget() {
        var properties = getInstrumentProperties();
        Optional<InstrumentPropertiesEntity> incomeBudgetIdProperty = properties.stream().filter(i->i.getPropertyname().equals(InstrumentPropertyType.INCOMEBUDGETID.getStringValue())).findFirst();
        if(!incomeBudgetIdProperty.isPresent()) {
            throw new MFException(MFMsgKey.NO_INCOMEBUDGET_DEFINED_EXCEPTION, "No IncomeBudget defined for budgetGroupId:"+instrumentId);
        }
        String incomeBudgetId = incomeBudgetIdProperty.get().getValue();
        var incomeBudget = instrumentRepository.findById(incomeBudgetId);
        if(!incomeBudget.isPresent()) {
            throw new MFException(MFMsgKey.NO_INCOMEBUDGET_DEFINED_EXCEPTION, "the IncomeBudget with id:"+incomeBudgetId+" does not exists");
        }
        
        return incomeBudget.get();
    } 

    @Override
    protected void saveNewInstrument(){
        super.saveNewInstrument();
        var budgetHandler = instrumentFactory.getInstrumentHandler(InstrumentType.BUDGET, DEFAULT_INCOMEBUDGET_PREFIX + domainObject.getDescription(), instrumentId, DEFAULT_INCOMEBUDGET_PREFIX + domainObject.getBusinesskey());
        budgetHandler.setTreeLastChanged(ts);
        budgetHandler.save();
        addProperty(InstrumentPropertyType.INCOMEBUDGETID, budgetHandler.getInstrumentId());
    }

    @Override
    protected void createDomainObject() {
        domainObject = new InstrumentEntity(InstrumentType.BUDGETGROUP, description, true, ts);
    }

    @Override
    protected void setDomainObjectName() {
        domainObjectName = "BudgetGroup";
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.BUDGETGROUP;
    }

    @Override
    protected void updateInstrument() {
        super.updateInstrument();
        var incomeBudget = getIncomeBudget();
        var handler = instrumentFactory.getInstrumentHandler(incomeBudget.getInstrumentid());
        handler.setDescription(DEFAULT_INCOMEBUDGET_PREFIX + domainObject.getDescription());
        handler.save();
    }

    @Override
    protected void validateInstrument4Inactivation() {
        for(InstrumentEntity budget : getInstrumentChilds(EdgeType.TENANTGRAPH, 1)) {
            var budgetHandler = instrumentFactory.getInstrumentHandler(budget.getInstrumentid());
            budgetHandler.setActive(false);
            budgetHandler.save();
        }
    }

    @Override
    protected InstrumentType getParentType() {
        return InstrumentType.BUDGETPORTFOLIO;
    }
} 