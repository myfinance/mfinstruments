package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.service.InstrumentFactory;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithGraphAndFactory;
import de.hf.myfinance.restmodel.InstrumentType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class TenantHandler extends AbsAccountableInstrumentHandler {
    private InstrumentFactory instrumentFactory;

    private static final String DEFAULT_ACCPF_PREFIX = "accPf_";
    private static final String DEFAULT_BUDGETPF_PREFIX = "bgtPf_";
    private static final String DEFAULT_BUDGETGROUP_PREFIX = "bgtGrp_";


    public TenantHandler(InstrumentEnvironmentWithGraphAndFactory instrumentEnvironment, String description, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, null, businesskey, isNewInstrument);
        this.instrumentFactory = instrumentEnvironment.getInstrumentFactory();
        isRootElement = true;
    }

    @Override
    protected Mono<InstrumentEntity> saveNewInstrument(InstrumentEntity instrumentEntity) {
        return super.saveNewInstrument(instrumentEntity)
                .flatMap(e->{
                    var budgetPortfolioHandler = instrumentFactory.getInstrumentHandlerForNewInstrument(InstrumentType.BUDGETPORTFOLIO, DEFAULT_BUDGETPF_PREFIX+e.getDescription(), e.getBusinesskey());
                    budgetPortfolioHandler.setTreeLastChanged(ts);
                    return budgetPortfolioHandler.save()
                            .flatMap(bpf-> {
                                var budgetGroupHandler = instrumentFactory.getInstrumentHandlerForNewInstrument(InstrumentType.BUDGETGROUP, DEFAULT_BUDGETGROUP_PREFIX+e.getDescription(), bpf.getBusinesskey());
                                budgetGroupHandler.setTreeLastChanged(ts);
                                return budgetGroupHandler.save();
                            })
                            // Return again the mono of the tenant
                            .flatMap(bpf-> Mono.just(e));
                })
                .flatMap(e->{
                    var accPortfolioHandler = instrumentFactory.getInstrumentHandlerForNewInstrument(InstrumentType.ACCOUNTPORTFOLIO, DEFAULT_ACCPF_PREFIX+e.getDescription(), e.getBusinesskey());
                    accPortfolioHandler.setTreeLastChanged(ts);
                    return accPortfolioHandler.save()
                            // Return again the mono of the tenant
                            .flatMap(bpf-> Mono.just(e));
                });
    }



    public Mono<InstrumentEntity> getAccountPortfolio() {
        return listFirstLevelInstrumentChilds(InstrumentType.ACCOUNTPORTFOLIO, true).next();
    }

    public Mono<InstrumentEntity> getBudgetPortfolio() {
        return listFirstLevelInstrumentChilds(InstrumentType.BUDGETPORTFOLIO, true).next();
    }

    public Flux<InstrumentEntity> getAccounts() {
        return filterActiveInstrumentChilds( listInstrumentChilds(getAccountPortfolio(), 1));

    }

    @Override
    protected InstrumentEntity createDomainObject() {
        return new InstrumentEntity(InstrumentType.TENANT, description, true, ts);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.TENANT;
    }
}