package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.service.InstrumentFactory;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithFactory;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import reactor.core.publisher.Mono;

public class BudgetPortfolioHandler extends AbsAccountableInstrumentHandler {
    private InstrumentFactory instrumentFactory;

    private static final String DEFAULT_BUDGETGROUP_PREFIX = "bgtGrp_";
    
    public BudgetPortfolioHandler(InstrumentEnvironmentWithFactory instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
        this.instrumentFactory = instrumentEnvironment.getInstrumentFactory();
    }

    @Override
    protected Instrument createDomainObject() {
        return new Instrument(businesskey, requestedInstrument.getDescription(), InstrumentType.BUDGETPORTFOLIO, true, ts);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.BUDGETPORTFOLIO;
    }

    @Override
    protected Mono<String> postApproveAction(String msg){
        var budgetGroup = new Instrument(DEFAULT_BUDGETGROUP_PREFIX+requestedInstrument.getDescription(), InstrumentType.BUDGETGROUP);
        budgetGroup.setParentBusinesskey(businesskey);

        var budgetGroupHandler = (AccountableInstrumentHandler)instrumentFactory.getInstrumentHandler(budgetGroup);
        budgetGroupHandler.setTreeLastChanged(ts);
        budgetGroupHandler.setIsSimpleValidation(true);
        if(isSimpleValidation) {
            // block is ok here. Due to the simplevalidate the tenantbusinesskey is not read from the db but create with just
            budgetGroupHandler.setTenant(this.getTenant().block());
        }
        return budgetGroupHandler.save();
    }
}