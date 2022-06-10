package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.service.InstrumentFactory;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithGraphAndFactory;
import de.hf.myfinance.restmodel.InstrumentType;
import reactor.core.publisher.Mono;


public class BudgetGroupHandler extends AbsAccountableInstrumentHandler {
    private  final InstrumentFactory instrumentFactory;
    private static final String DEFAULT_INCOMEBUDGET_PREFIX = "incomeBgt_";

    public BudgetGroupHandler(InstrumentEnvironmentWithGraphAndFactory instrumentEnvironment, String description, String budgetPFId, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, budgetPFId, businesskey, isNewInstrument);
        this.instrumentFactory = instrumentEnvironment.getInstrumentFactory();
    }

    public InstrumentEntity getIncomeBudget() {
        /*var properties = getInstrumentProperties();
        Optional<InstrumentPropertiesEntity> incomeBudgetIdProperty = properties.stream().filter(i->i.getPropertyname().equals(InstrumentPropertyType.INCOMEBUDGETID.getStringValue())).findFirst();
        if(!incomeBudgetIdProperty.isPresent()) {
            throw new MFException(MFMsgKey.NO_INCOMEBUDGET_DEFINED_EXCEPTION, "No IncomeBudget defined for budgetGroupId:"+instrumentId);
        }
        String incomeBudgetId = incomeBudgetIdProperty.get().getValue();
        var incomeBudget = instrumentRepository.findById(incomeBudgetId);
        if(!incomeBudget.isPresent()) {
            throw new MFException(MFMsgKey.NO_INCOMEBUDGET_DEFINED_EXCEPTION, "the IncomeBudget with id:"+incomeBudgetId+" does not exists");
        }
        
        return incomeBudget.get();*/
        return null;
    } 

    @Override
    protected Mono<InstrumentEntity> saveNewInstrument(InstrumentEntity instrumentEntity){
        /*super.saveNewInstrument();
        var budgetHandler = instrumentFactory.getInstrumentHandler(InstrumentType.BUDGET, DEFAULT_INCOMEBUDGET_PREFIX + domainObject.getDescription(), instrumentId, null);
        budgetHandler.setTreeLastChanged(ts);
        budgetHandler.save();
        addProperty(InstrumentPropertyType.INCOMEBUDGETID, budgetHandler.getInstrumentId());*/
        return super.saveNewInstrument(instrumentEntity)
                .flatMap(e->{
                    var budgetHandler = instrumentFactory.getInstrumentHandlerForNewInstrument(InstrumentType.BUDGET, DEFAULT_INCOMEBUDGET_PREFIX+e.getDescription(), e.getInstrumentid());
                    budgetHandler.setTreeLastChanged(ts);
                    return budgetHandler.save()
                            // Return again the mono of the tenant
                            .flatMap(bpf-> Mono.just(e));
                });
    }

    @Override
    protected InstrumentEntity createDomainObject() {
        return new InstrumentEntity(InstrumentType.BUDGETGROUP, description, true, ts);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.BUDGETGROUP;
    }

    //@Override
    protected void updateInstrument() {
        /*super.updateInstrument();
        var incomeBudget = getIncomeBudget();
        var handler = instrumentFactory.getInstrumentHandler(incomeBudget.getInstrumentid());
        handler.setDescription(DEFAULT_INCOMEBUDGET_PREFIX + domainObject.getDescription());
        handler.save();*/
    }

    //@Override
    protected void validateInstrument4Inactivation() {
        /*for(InstrumentEntity budget : getInstrumentChilds(EdgeType.TENANTGRAPH, 1)) {
            var budgetHandler = instrumentFactory.getInstrumentHandlerForNewInstrument(budget.getInstrumentid());
            budgetHandler.setActive(false);
            budgetHandler.save();
        }*/
    }

    @Override
    protected InstrumentType getParentType() {
        return InstrumentType.BUDGETPORTFOLIO;
    }
} 