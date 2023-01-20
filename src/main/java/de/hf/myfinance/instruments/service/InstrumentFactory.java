package de.hf.myfinance.instruments.service;

import de.hf.framework.audit.AuditService;
import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.events.out.InstrumentApprovedEventHandler;
import de.hf.myfinance.instruments.persistence.DataReader;
import de.hf.myfinance.instruments.service.accountableinstrumenthandler.*;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentImpl;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithFactory;
import de.hf.myfinance.instruments.service.securityhandler.CurrencyHandler;
import de.hf.myfinance.instruments.service.securityhandler.EquityHandler;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class InstrumentFactory {

    private final InstrumentEnvironmentWithFactory instrumentEnvironment;

    @Autowired
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
            default -> throw new MFException(MFMsgKey.UNKNOWN_INSTRUMENTTYPE_EXCEPTION, "can not create Instrumenthandler for instrumentType:" + instrument.getInstrumentType());
        };
    }

    /**
     * loads the instrument for the instrumentId and returns an InstrumentHandler for the type of the instrument.
     * use this if you do not know the type of the instrument and the InstrumentHandler Interface is suffitioned for your purpose (so you do not need type spezific functions)
     * @param businesskey the businesskey of the instrument
     * @return Instrumenthandler for the instrumenttype of the new instrument
     */
    public InstrumentHandler getInstrumentHandlerForExistingInstrument(String businesskey) {
        InstrumentType instrumentType;
        try {
            int typeId = Integer.parseInt(businesskey.substring(businesskey.lastIndexOf("@")+1));
            instrumentType = InstrumentType.getInstrumentTypeById(typeId);
        } catch (Exception e) {
            throw new MFException(MFMsgKey.UNKNOWN_INSTRUMENTTYPE_EXCEPTION, " no valid businesskey, the Instrumenttype seems not to be included:"+businesskey);
        }
        var instrument = new Instrument(businesskey);
        instrument.setInstrumentType(instrumentType);
        return getInstrumentHandler(instrument);
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
}