package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.service.InstrumentFactory;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithFactory;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import reactor.core.publisher.Mono;

public class BudgetPortfolioHandler extends AbsAccountableInstrumentHandler {
    private InstrumentFactory instrumentFactory;

    private static final String DEFAULT_BUDGETGROUP_PREFIX = "bgtGrp_";
    
    public BudgetPortfolioHandler(InstrumentEnvironmentWithFactory instrumentEnvironment, String description, String tenantId, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, tenantId, businesskey, isNewInstrument);
        this.instrumentFactory = instrumentEnvironment.getInstrumentFactory();
    }

    @Override
    protected Instrument createDomainObject() {
        return new Instrument(businesskey, description, InstrumentType.BUDGETPORTFOLIO, true, ts);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.BUDGETPORTFOLIO;
    }

    @Override
    protected Mono<String> saveNewInstrument(Instrument instrument) {
        return super.saveNewInstrument(instrument)
                .flatMap(e->{
                    var budgetGroupHandler = instrumentFactory.getInstrumentHandlerForNewInstrument(InstrumentType.BUDGETGROUP, DEFAULT_BUDGETGROUP_PREFIX+description, businesskey, null);
                    budgetGroupHandler.setTreeLastChanged(ts);
                    return budgetGroupHandler.save().flatMap(bg-> Mono.just(e));
                });
    }
}