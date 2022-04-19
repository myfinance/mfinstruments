package de.hf.myfinance.instruments.service;

import java.time.LocalDateTime;
import java.util.Optional;

import de.hf.framework.audit.AuditService;
import de.hf.framework.audit.Severity;
import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.restmodel.InstrumentType;

/**
 * Base class for alle instrument handler
 */
public abstract class AbsInstrumentHandler {
    protected InstrumentRepository instrumentRepository;
    protected String instrumentId;
    protected boolean initialized = false;
    protected boolean existenceChecked = false;
    protected boolean exists = false;
    protected InstrumentEntity domainObject;
    protected boolean isPropertyInit = false;
    protected AuditService auditService;
    protected LocalDateTime ts;
    protected static final String AUDIT_MSG_TYPE="InstrumentHandler_User_Event";
    protected String domainObjectName;
    protected String description = "";
    protected String businesskey = "";
    protected String oldDesc;
    protected boolean isActive = true;

    private final int MAX_BUSINESSKEY_SIZE = 32;

    protected AbsInstrumentHandler(InstrumentRepository instrumentRepository, AuditService auditService, String description, String businesskey) {
        setBaseValues(instrumentRepository, auditService);
        setDescription(description);
        setBusinesskey(businesskey);
        loadInstrumentByBusinesskey();
    }

    protected AbsInstrumentHandler(InstrumentRepository instrumentRepository, AuditService auditService, String businesskey) {
        setBaseValues(instrumentRepository, auditService);
        setBusinesskey(businesskey);
        loadInstrumentByBusinesskey();
        if(!exists) {
            throw new MFException(MFMsgKey.UNKNOWN_INSTRUMENT_EXCEPTION, "Instrument for businesskey:"+businesskey + " does not exists. You can only create existing instruments with just a businesskey");
        }
    }

    private void setBaseValues(InstrumentRepository instrumentRepository, AuditService auditService) {
        this.instrumentRepository = instrumentRepository;
        this.auditService = auditService;
        ts = LocalDateTime.now();
    }

    protected void validateInstrument() {
        if(!domainObject.getInstrumentType().equals(getInstrumentType())) {
            throw new MFException(MFMsgKey.WRONG_INSTRUMENTTYPE_EXCEPTION, "can not create instrumenthandler for instrumentid:"+instrumentId);
        }
    }

    protected void validateInstrument(InstrumentEntity instrument, InstrumentType instrumentType, String errMsg) {
        if(instrument.getInstrumentType()!=instrumentType){
            throw new MFException(MFMsgKey.WRONG_INSTRUMENTTYPE_EXCEPTION, errMsg+" instrument has wrong type:"+instrument.getInstrumentType());
        }
    }

    public void setTreeLastChanged(LocalDateTime ts){
        this.ts = ts;
    }
    
    public void setInstrumentId(String instrumentId) {
        initialized = true;
        this.instrumentId = instrumentId;
    }
    public String getInstrumentId() {
        return this.instrumentId;
    }

    protected void checkInitStatus() {
        if(!initialized) {
            throw new MFException(MFMsgKey.OBJECT_NOT_INITIALIZED_EXCEPTION, "instrumentId is not set:");
        }
    }

    protected void checkDomainObjectInitStatus() {
        if(this.domainObject==null) {
            throw new MFException(MFMsgKey.OBJECT_NOT_INITIALIZED_EXCEPTION, "instrument is not set:");
        }
    }

    protected void loadInstrumentByBusinesskey() {
        this.domainObject = instrumentRepository.findByBusinesskey(businesskey);
        loadInstrument();
    }

    private void loadInstrument() {
        this.domainObject = instrumentRepository.findByBusinesskey(businesskey);
        existenceChecked = true;
        if(this.domainObject!=null) {
            exists = true;
            setInstrumentId(this.domainObject.getInstrumentid());
            validateInstrument();
        } else {
            createDomainObject();
            domainObject.setBusinesskey(this.businesskey);
        }
    }

    /**
     * get and validate an Instrument for another id. the instrumentId of the Instrumenthandler will not change
     * @param instrumentId the id
     * @return the instrument for the id
     */
    protected InstrumentEntity getInstrumentById(String instrumentId) {
        return getInstrumentById(instrumentId, "");
    }

    protected InstrumentEntity getInstrumentById(String instrumentId, String errMsg) {
        var instrument = instrumentRepository.findById(instrumentId);
        if(!instrument.isPresent()){
            throw new MFException(MFMsgKey.UNKNOWN_INSTRUMENT_EXCEPTION, errMsg + " Instrument for id:"+instrumentId + " not found");
        }
        return instrument.get();
    }


    protected void checkInstrumentInactivation(boolean isActiveBeforeUpdate,  boolean isActiveAfterUpdate) {
        // try to deactivate instrument ?
        if(!isActiveAfterUpdate && isActiveBeforeUpdate) {
            validateInstrument4Inactivation();
        }        
    } 

    protected void validateInstrument4Inactivation() {
        throw new MFException(MFMsgKey.NO_VALID_INSTRUMENT_FOR_DEACTIVATION, "instrument with id:"+instrumentId + " not deactivated. Instruments with type:"+ domainObject.getInstrumentType() + " can not deactivated");
    }

    public void save() {
        loadInstrument();
        if(exists) {
            updateInstrument();
        } else {
            saveNewInstrument();
            exists = true;
        }
    } 

    protected void saveNewInstrument() {
        checkDomainObjectInitStatus();
        instrumentRepository.save(domainObject);
        setInstrumentId(domainObject.getInstrumentid());
        auditService.saveMessage(domainObjectName+" inserted:" + domainObject.getDescription(), Severity.INFO, AUDIT_MSG_TYPE);
    }

    protected void updateInstrument() {
        checkInstrumentInactivation(domainObject.isIsactive(), isActive);
        oldDesc = domainObject.getDescription();
        if(description==null || description.equals("")) {
            description = domainObject.getDescription();
        }
        if(businesskey.equals("")) {
            businesskey = domainObject.getBusinesskey();
        }
        //instrumentRepository.updateInstrument(instrumentId, description, isActive, businesskey);
        auditService.saveMessage(domainObjectName+" updated:" + domainObject.getDescription(), Severity.INFO, AUDIT_MSG_TYPE);
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBusinesskey(String businesskey) {
        if(businesskey==null) {
            this.businesskey = description.trim();
        } else {
            this.businesskey = businesskey.trim();
        }
        this.businesskey = this.businesskey.replace(" ", "");
        if(this.businesskey.length()> MAX_BUSINESSKEY_SIZE) this.businesskey = this.businesskey.substring(0, MAX_BUSINESSKEY_SIZE);
        this.businesskey = this.businesskey+"@"+getInstrumentType().getValue();
    }

    public Optional<InstrumentEntity> getSavedDomainObject() {
        if(exists) return Optional.of(domainObject);
        return Optional.empty();
    }

    abstract protected void createDomainObject();
    abstract protected void setDomainObjectName();
    abstract protected InstrumentType getInstrumentType();
}