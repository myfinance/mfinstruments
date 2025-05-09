package de.hf.myfinance.instruments.service;

import de.hf.framework.audit.AuditService;
import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.events.out.InstrumentApprovedEventHandler;
import de.hf.myfinance.instruments.persistence.DataReader;
import de.hf.myfinance.instruments.service.accountableinstrumenthandler.*;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentImpl;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithFactory;
import de.hf.myfinance.instruments.service.securityhandler.BondHandler;
import de.hf.myfinance.instruments.service.securityhandler.CurrencyHandler;
import de.hf.myfinance.instruments.service.securityhandler.EquityHandler;
import de.hf.myfinance.instruments.service.securityhandler.EtfHandler;
import de.hf.myfinance.instruments.service.securityhandler.FondHandler;
import de.hf.myfinance.instruments.service.securityhandler.IndexHandler;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class InstrumentFactory {

    private final InstrumentEnvironmentWithFactory instrumentEnvironment;

    public InstrumentFactory(DataReader dataReader, AuditService auditService, InstrumentApprovedEventHandler eventHandler) {
        instrumentEnvironment = new InstrumentEnvironmentImpl(dataReader, auditService, this, eventHandler);
    }

    /**
     * creates an Instrumenthandler for a new Instrument
     * @param instrument the instrument for that we need the handler
     * @return Instrumenthandler for the instrumenttype of the new instrument
     */
    public InstrumentHandler getInstrumentHandler(Instrument instrument) {
        return switch (instrument.getInstrumentType()) {
            case TENANT -> new TenantHandler(instrumentEnvironment, instrument);
            case BUDGETPORTFOLIO -> new BudgetPortfolioHandler(instrumentEnvironment, instrument);
            case ACCOUNTPORTFOLIO -> new AccountPortfolioHandler(instrumentEnvironment, instrument);
            case BUDGETGROUP -> new BudgetGroupHandler(instrumentEnvironment, instrument);
            case BUDGET -> new BudgetHandler(instrumentEnvironment, instrument);
            case GIRO -> new GiroHandler(instrumentEnvironment, instrument);
            case CURRENCY -> new CurrencyHandler(instrumentEnvironment, instrument);
            case EQUITY -> new EquityHandler(instrumentEnvironment, instrument);
            case DEPOT -> new DepotHandler(instrumentEnvironment, instrument);
            case REALESTATE -> new RealestateHandler(instrumentEnvironment, instrument);
            case DEPRECATIONOBJECT -> new DeprecationObjectHandler(instrumentEnvironment, instrument);
            case LIFEINSURANCE -> new LifeInsuranceHandler(instrumentEnvironment, instrument);
            case LOAN -> new LoanHandler(instrumentEnvironment, instrument);
            case MONEYATCALL -> new MoneyAtCallHandler(instrumentEnvironment, instrument);
            case TIMEDEPOSIT -> new TimeDepositHandler(instrumentEnvironment, instrument);
            case BUILDINGSAVINGACCOUNT -> new BuildingsavingAcoountHandler(instrumentEnvironment, instrument);
            case ETF -> new EtfHandler(instrumentEnvironment, instrument);
            case INDEX -> new IndexHandler(instrumentEnvironment, instrument);
            case FONDS -> new FondHandler(instrumentEnvironment, instrument);
            case BOND -> new BondHandler(instrumentEnvironment, instrument);
            default -> throw new MFException(MFMsgKey.UNKNOWN_INSTRUMENTTYPE_EXCEPTION, "can not create Instrumenthandler for instrumentType:" + instrument.getInstrumentType());
        };
    }

    /**
     * returns an TenantHandler. 
     * use this or the following InstrumentHandlerType-Spezific functions, if you know exactly what kind of instrumenthandler you want and if it matters. 
     * E.G. The TenantHandler has spezific public functions. You can only use them if you know that the instrumentId is a Tenant and you get the handler for this
     * @param businesskey the businesskey of the tenant
     * @return TenantHandler
     */
    public TenantHandler getTenantHandler(String businesskey) {
        var instrument = new Instrument(businesskey);
        instrument.setInstrumentType(InstrumentType.TENANT);
        return new TenantHandler(instrumentEnvironment, instrument);
    }

    public Flux<Instrument> listInstruments() {
        return instrumentEnvironment.getDataReader().findAll();
    }

    public Flux<Instrument> listTenants(){
        return listInstruments().filter(i->i.getInstrumentType().equals(InstrumentType.TENANT));
    }

    public Mono<Instrument> loadInstrument(String businesskey) {
        return instrumentEnvironment.getDataReader().findByBusinesskey(businesskey);
    }
}