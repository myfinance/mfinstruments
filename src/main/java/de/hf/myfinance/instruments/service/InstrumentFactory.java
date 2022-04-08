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
import de.hf.myfinance.instruments.service.accountableinstrumenthandler.BaseAccountableInstrumentHandler;
import de.hf.myfinance.instruments.service.accountableinstrumenthandler.BaseAccountableInstrumentHandlerImpl;
import de.hf.myfinance.instruments.service.accountableinstrumenthandler.TenantHandler;
import de.hf.myfinance.restmodel.InstrumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InstrumentFactory {

    private final InstrumentRepository instrumentRepository;
    private final InstrumentGraphRepository instrumentGraphRepository;
    private final AuditService auditService;

    @Autowired
    public InstrumentFactory(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService) {
        this.instrumentRepository = instrumentRepository;
        this.instrumentGraphRepository = instrumentGraphRepository;
        this.auditService = auditService;
    }

    /**
     * returns a baseinstrumenthandler with type indifferent functions like getTenant().
     * use this if you do not know the type and it doesn't matter for your purpose
     * @param instrumentId the id of the instrument
     * @return the BaseAccountableInstrumentHandler
     */
    public BaseAccountableInstrumentHandler getBaseInstrumentHandler(String instrumentId) {
        return new BaseAccountableInstrumentHandlerImpl(instrumentRepository, instrumentGraphRepository, auditService, instrumentId);
    }

    /**
     * creates an Instrumenthandler for a new Instrument
     * @param instrumentType the type of the new instrument
     * @param description the description
     * @param parentId the id of the parent
     * @return Instrumenthandler for the instrumenttype of the new instrument
     */
    public InstrumentHandler getInstrumentHandler(InstrumentType instrumentType, String description, String parentId, String businesskey) {
        switch(instrumentType){
            case TENANT:
                return new TenantHandler(instrumentRepository, instrumentGraphRepository, auditService, this, description);
            default:
                throw new MFException(MFMsgKey.UNKNOWN_INSTRUMENTTYPE_EXCEPTION, "can not create Instrumenthandler for instrumentType:"+instrumentType);
        }
    }

    /**
     * loads the instrument for the instrumentId and returns an InstrumentHandler for the type of the instrument.
     * use this if you do not know the type of the instrument and the InstrumentHandler Interface is suffitioned for your purpose (so you do not need type spezific functions)
     * @param instrumentId the id of the instrument
     * @return Instrumenthandler for the instrumenttype of the new instrument
     */
    public InstrumentHandler getInstrumentHandler(String instrumentId) {
        var instrument =  getBaseInstrumentHandler(instrumentId).getInstrument();
        switch(instrument.getInstrumentType()){
            case TENANT:
                return new TenantHandler(instrumentRepository, instrumentGraphRepository, auditService, this, instrument);
            default:
                throw new MFException(MFMsgKey.UNKNOWN_INSTRUMENTTYPE_EXCEPTION, "can not create Instrumenthandler for instrumentType:"+instrument.getInstrumentType());
        }
    }

    /**
     * returns an TenantHandler. 
     * use this or the following InstrumentHandlerType-Spezific functions, if you know exactly what kind of instrumenthandler you want and if it matters. 
     * E.G. The TenantHandler has spezific public functions. You can only use them if you know that the instrumentId is a Tenant and you get the handler for this
     * @param instrumentId the instrument id
     * @param validate true if you want to validate that the type of the instrument and the expected type fits together (should be true in case you plan write operations with this instrument. Otherwise it is faster without validation)
     * @return TenantHandler
     */
    public TenantHandler getTenantHandler(String instrumentId, boolean validate) {
        var handler = new TenantHandler(instrumentRepository, instrumentGraphRepository, auditService, instrumentId, this);
        if(validate) handler.validateInstrument();
        return handler;
    }

    public Iterable<InstrumentEntity> listInstruments() {
        return instrumentRepository.findAll();
    }

    public List<InstrumentEntity> listTenants(){
        return StreamSupport.stream(listInstruments().spliterator(), false).filter(i->i.getInstrumentType().equals(InstrumentType.TENANT)).collect(Collectors.toList());
    }
}