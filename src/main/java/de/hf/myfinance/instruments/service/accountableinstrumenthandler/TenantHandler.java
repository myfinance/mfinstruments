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

    public TenantHandler(InstrumentEnvironmentWithFactory instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
        this.instrumentFactory = instrumentEnvironment.getInstrumentFactory();
        isRootElement = true;
    }

    @Override
    protected Mono<String> postApproveAction(String msg){
        if(isNewInstrument){
            return Mono.just(msg)
                .flatMap(this::saveBudgetPortfolio)
                .flatMap(this::saveAccPortfolio);
        }
        return Mono.just(msg);
    }

    private Mono<String> saveBudgetPortfolio(String msg) {
        var budgetPortfolio = new Instrument(DEFAULT_BUDGETPF_PREFIX+requestedInstrument.getDescription(), InstrumentType.BUDGETPORTFOLIO);
        budgetPortfolio.setParentBusinesskey(businesskey);
        var budgetPortfolioHandler = (AccountableInstrumentHandler)instrumentFactory.getInstrumentHandler(budgetPortfolio);
        budgetPortfolioHandler.setTreeLastChanged(ts);
        budgetPortfolioHandler.setIsSimpleValidation(true);
        budgetPortfolioHandler.setTenant(this.businesskey);
        return budgetPortfolioHandler.save();
    }

    private Mono<String> saveAccPortfolio(String msg) {
        var accPortfolio = new Instrument(DEFAULT_ACCPF_PREFIX+requestedInstrument.getDescription(), InstrumentType.ACCOUNTPORTFOLIO);
        accPortfolio.setParentBusinesskey(businesskey);
        var accPortfolioHandler = (AccountableInstrumentHandler)instrumentFactory.getInstrumentHandler(accPortfolio);
        accPortfolioHandler.setTreeLastChanged(ts);
        accPortfolioHandler.setIsSimpleValidation(true);
        accPortfolioHandler.setTenant(this.businesskey);
        return accPortfolioHandler.save();
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

    public Flux<Instrument> getBudgets() {
        return filterActiveInstrumentChilds( listInstrumentChilds(getBudgetPortfolio(), 2));
    }

    @Override
    protected Instrument createDomainObject() {
        return new Instrument(businesskey, requestedInstrument.getDescription(), InstrumentType.TENANT, true, ts);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.TENANT;
    }

    // you can allways inactivate a tenant
    protected Mono<Instrument> validateInstrument4Inactivation(Instrument instrument) {
        instrument.setActive(false);
        return Mono.just(instrument);
    }
}