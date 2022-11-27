package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.service.InstrumentFactory;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithFactory;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import reactor.core.publisher.Mono;


public class BudgetGroupHandler extends AbsAccountableInstrumentHandler {
    private  final InstrumentFactory instrumentFactory;
    private static final String DEFAULT_INCOMEBUDGET_PREFIX = "incomeBgt_";

    public BudgetGroupHandler(InstrumentEnvironmentWithFactory instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
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
    protected Mono<String> postApproveAction(String msg){
        var budget = new Instrument(DEFAULT_INCOMEBUDGET_PREFIX+requestedInstrument.getDescription(), InstrumentType.BUDGET);
        budget.setParentBusinesskey(businesskey);
        var budgetHandler = (AccountableInstrumentHandler)instrumentFactory.getInstrumentHandler(budget);
        budgetHandler.setTreeLastChanged(ts);
        budgetHandler.setIsSimpleValidation(true);

        if(isSimpleValidation) {
            // block is ok here. Due to the simplevalidate the tenantbusinesskey is not read from the db but create with just
            budgetHandler.setTenant(this.getTenant().block());
        }
        return budgetHandler.save();
    }

    @Override
    protected Instrument createDomainObject() {
        return new Instrument(businesskey, requestedInstrument.getDescription(), InstrumentType.BUDGETGROUP, true, ts);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.BUDGETGROUP;
    }


    @Override
    protected InstrumentType getParentType() {
        return InstrumentType.BUDGETPORTFOLIO;
    }
} 