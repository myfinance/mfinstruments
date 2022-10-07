package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.service.InstrumentFactory;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithFactory;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class TenantHandler extends AbsAccountableInstrumentHandler {
    private InstrumentFactory instrumentFactory;

    private static final String DEFAULT_ACCPF_PREFIX = "accPf_";
    private static final String DEFAULT_BUDGETPF_PREFIX = "bgtPf_";

    public TenantHandler(InstrumentEnvironmentWithFactory instrumentEnvironment, String description, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, null, businesskey, isNewInstrument);
        this.instrumentFactory = instrumentEnvironment.getInstrumentFactory();
        isRootElement = true;
    }

    @Override
    protected Mono<String> saveNewInstrument(Instrument instrument) {
        return super.saveNewInstrument(instrument)
                .flatMap(e->{
                    var budgetPortfolioHandler = instrumentFactory.getInstrumentHandlerForNewInstrument(InstrumentType.BUDGETPORTFOLIO, DEFAULT_BUDGETPF_PREFIX+description, businesskey, null);
                    budgetPortfolioHandler.setTreeLastChanged(ts);
                    return budgetPortfolioHandler.save().flatMap(bpf-> Mono.just(e));
                })
                .flatMap(e->{
                    var accPortfolioHandler = instrumentFactory.getInstrumentHandlerForNewInstrument(InstrumentType.ACCOUNTPORTFOLIO, DEFAULT_ACCPF_PREFIX+description, businesskey, null);
                    accPortfolioHandler.setTreeLastChanged(ts);
                    return accPortfolioHandler.save()
                            // Return again the mono of the tenant
                            .flatMap(bpf-> Mono.just(e));
                });
    }



    public Mono<Instrument> getAccountPortfolio() {
        return listFirstLevelInstrumentChilds(InstrumentType.ACCOUNTPORTFOLIO, true).next();
    }

    public Mono<Instrument> getBudgetPortfolio() {
        return listFirstLevelInstrumentChilds(InstrumentType.BUDGETPORTFOLIO, true).next();
    }

    public Flux<Instrument> getAccounts() {
        return filterActiveInstrumentChilds( listInstrumentChilds(getAccountPortfolio(), 1));

    }

    @Override
    protected Instrument createDomainObject() {
        return new Instrument(businesskey, description, InstrumentType.TENANT, true, ts);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.TENANT;
    }
}