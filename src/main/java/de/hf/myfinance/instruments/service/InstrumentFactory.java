package de.hf.myfinance.instruments.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import de.hf.framework.audit.AuditService;
import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.service.accountableinstrumenthandler.*;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentImpl;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithGraphAndFactory;
import de.hf.myfinance.restmodel.InstrumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InstrumentFactory {

    private final InstrumentEnvironmentWithGraphAndFactory instrumentEnvironment;

    @Autowired
    public InstrumentFactory(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService) {
        instrumentEnvironment = new InstrumentEnvironmentImpl(instrumentRepository, instrumentGraphRepository, auditService, this);
    }

    /**
     * returns a baseinstrumenthandler with type indifferent functions like getTenant().
     * use this if you do not know the type and it doesn't matter for your purpose
     * @param instrumentId the id of the instrument
     * @return the BaseAccountableInstrumentHandler
     */
    public BaseAccountableInstrumentHandler getBaseInstrumentHandler(String instrumentId) {
        return new BaseAccountableInstrumentHandlerImpl(instrumentEnvironment, instrumentId);
    }

    /**
     * creates an Instrumenthandler for a new Instrument
     * @param instrumentType the type of the new instrument
     * @param description the description
     * @param parentId the id of the parent
     * @return Instrumenthandler for the instrumenttype of the new instrument
     */
    public InstrumentHandler getInstrumentHandlerForNewInstrument(InstrumentType instrumentType, String description, String parentId) {
        switch(instrumentType){
            case TENANT:
                return new TenantHandler(instrumentEnvironment, description, null, true);
            case BUDGETPORTFOLIO:
                return new BudgetPortfolioHandler(instrumentEnvironment, description, parentId, null, true);
            case ACCOUNTPORTFOLIO:
                return new AccountPortfolioHandler(instrumentEnvironment, description, parentId, null, true);
            case BUDGETGROUP:
                return new BudgetGroupHandler(instrumentEnvironment, description, parentId, null, true);
            case BUDGET:
                return new BudgetHandler(instrumentEnvironment, description, parentId, null, true);

            default:
                throw new MFException(MFMsgKey.UNKNOWN_INSTRUMENTTYPE_EXCEPTION, "can not create Instrumenthandler for instrumentType:"+instrumentType);
        }
    }

    /**
     * loads the instrument for the instrumentId and returns an InstrumentHandler for the type of the instrument.
     * use this if you do not know the type of the instrument and the InstrumentHandler Interface is suffitioned for your purpose (so you do not need type spezific functions)
     * @param businesskey the businesskey of the instrument
     * @return Instrumenthandler for the instrumenttype of the new instrument
     */
    public InstrumentHandler getInstrumentHandlerForExistingInstrument(String businesskey) {
        InstrumentType instrumentType = InstrumentType.UNKNOWN;
        try {
            int typeId = Integer.parseInt(businesskey.substring(businesskey.lastIndexOf("@")+1));
            instrumentType = InstrumentType.getInstrumentTypeById(typeId);
        } catch (Exception e) {
            throw new MFException(MFMsgKey.UNKNOWN_INSTRUMENTTYPE_EXCEPTION, " no valid businesskey, the Instrumenttype seems not to be included:"+businesskey);
        }
        switch(instrumentType){
            case TENANT:
                return new TenantHandler(instrumentEnvironment, null, businesskey, false);
            case BUDGETPORTFOLIO:
                return new BudgetPortfolioHandler(instrumentEnvironment, null, null, businesskey, false);
            case ACCOUNTPORTFOLIO:
                return new AccountPortfolioHandler(instrumentEnvironment, null, null, businesskey, false);
            case BUDGETGROUP:
                return new BudgetGroupHandler(instrumentEnvironment, null, null, businesskey, false);
            case BUDGET:
                return new BudgetHandler(instrumentEnvironment, null, null, businesskey, false);
            default:
                throw new MFException(MFMsgKey.UNKNOWN_INSTRUMENTTYPE_EXCEPTION, "can not create Instrumenthandler for instrumentType:"+instrumentType);
        }
    }

    /**
     * returns an TenantHandler. 
     * use this or the following InstrumentHandlerType-Spezific functions, if you know exactly what kind of instrumenthandler you want and if it matters. 
     * E.G. The TenantHandler has spezific public functions. You can only use them if you know that the instrumentId is a Tenant and you get the handler for this
     * @param businesskey the businesskey of the tenant
     * @return TenantHandler
     */
    public TenantHandler getTenantHandler(String businesskey) {
        return new TenantHandler(instrumentEnvironment, null, businesskey, false);
    }

    public Iterable<InstrumentEntity> listInstruments() {
        return null;//instrumentRepository.findAll();
    }

    public List<InstrumentEntity> listTenants(){
        return StreamSupport.stream(listInstruments().spliterator(), false).filter(i->i.getInstrumentType().equals(InstrumentType.TENANT)).collect(Collectors.toList());
    }
}