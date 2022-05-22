package de.hf.myfinance.instruments.service;

import java.time.LocalDateTime;

import de.hf.framework.audit.AuditService;
import de.hf.framework.audit.Severity;
import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.InstrumentType;
import reactor.core.publisher.Mono;

/**
 * Base class for alle instrument handler
 */
public abstract class AbsInstrumentHandler {
    protected final InstrumentRepository instrumentRepository;
    private final AuditService auditService;
    protected String instrumentId;
    protected boolean initialized = false;
    protected boolean exists = true;
    protected Mono<InstrumentEntity> domainObjectMono;
    protected boolean isPropertyInit = false;
    protected LocalDateTime ts;
    protected static final String AUDIT_MSG_TYPE="InstrumentHandler_User_Event";
    protected String domainObjectName;
    protected String description = "";
    protected String businesskey = "";
    protected String oldDesc;
    protected boolean isActive = true;
    protected boolean isNewInstrument;

    private final int MAX_BUSINESSKEY_SIZE = 32;

    protected AbsInstrumentHandler(InstrumentEnvironment instrumentEnvironment, String description, String businesskey, boolean isNewInstrument) {
        this.instrumentRepository = instrumentEnvironment.getInstrumentRepository();
        this.auditService = instrumentEnvironment.getAuditService();
        ts = LocalDateTime.now();
        this.description = description;
        this.businesskey = businesskey;
        this.isNewInstrument = isNewInstrument;
        setBusinesskey();
        loadInstrument(isNewInstrument);
    }

    protected void loadInstrument(boolean isNewInstrument) {
        this.domainObjectMono = instrumentRepository.findByBusinesskey(businesskey)
                .switchIfEmpty(handleNotExistingInstrument(isNewInstrument))
                .map(e -> {
                    validateInstrument(e, getInstrumentType(), "");
                    return e;
                })
                .log();
    }

    private Mono<InstrumentEntity> handleNotExistingInstrument(boolean isNewInstrument){
        if(isNewInstrument) {
            return initNewDomainObject();
        } else {
            return Mono.error(new MFException(MFMsgKey.UNKNOWN_INSTRUMENT_EXCEPTION, "Instrument for businesskey:"+businesskey + " does not exists."));
        }
    }

    protected Mono<InstrumentEntity> initNewDomainObject() {
        var object = createDomainObject();
        object.setBusinesskey(this.businesskey);
        exists = false;
        return Mono.just(object);
    }

    protected void validateInstrument(InstrumentEntity instrument, InstrumentType instrumentType, String errMsg) {
        if(instrument.getInstrumentType()!=instrumentType){
            throw new MFException(MFMsgKey.WRONG_INSTRUMENTTYPE_EXCEPTION, errMsg+" instrument has wrong type:"+instrument.getInstrumentType());
        }
    }

    public Mono<InstrumentEntity> save() {
        return domainObjectMono.flatMap(this::saveOrUpdate);
    }

    private Mono<InstrumentEntity> saveOrUpdate(InstrumentEntity instrumentEntity) {
        Mono<InstrumentEntity> newDomainObjectmono;
        if(exists) {
            newDomainObjectmono=updateInstrument(instrumentEntity);
        } else {
            newDomainObjectmono=saveNewInstrument(instrumentEntity);
            exists = true;
        }
        return newDomainObjectmono;
    }

    protected Mono<InstrumentEntity> saveNewInstrument(InstrumentEntity instrumentEntity) {
        var newDomainObjectmono = instrumentRepository.save(instrumentEntity);
        auditService.saveMessage(domainObjectName+" inserted: businesskey=" + instrumentEntity.getBusinesskey() + " desc=" + instrumentEntity.getDescription(), Severity.INFO, AUDIT_MSG_TYPE);
        return newDomainObjectmono;
    }

    protected Mono<InstrumentEntity> updateInstrument(InstrumentEntity instrumentEntity) {
        checkInstrumentInactivation(instrumentEntity, isActive);
        oldDesc = instrumentEntity.getDescription();
        if (description != null && !description.equals("")) {
            instrumentEntity.setDescription(description);
        }
        var newDomainObjectmono = instrumentRepository.save(instrumentEntity);
        auditService.saveMessage(domainObjectName + " updated:businesskey=" + instrumentEntity.getBusinesskey() + " desc=" + instrumentEntity.getDescription(), Severity.INFO, AUDIT_MSG_TYPE);
        return newDomainObjectmono;
    }

        protected Mono<InstrumentEntity> getInstrumentById(String instrumentId, String errMsg) {
        var instrument = instrumentRepository.findById(instrumentId)
                .switchIfEmpty(
                    Mono.error(new MFException(MFMsgKey.UNKNOWN_INSTRUMENT_EXCEPTION, errMsg + " Instrument for id:" + instrumentId + " not found")));

        return instrument;
    }


    protected void checkInstrumentInactivation(InstrumentEntity oldInstrumentEntity,  boolean isActiveAfterUpdate) {
        // try to deactivate instrument ?

        if(!isActiveAfterUpdate && oldInstrumentEntity.isIsactive()) {
            validateInstrument4Inactivation(oldInstrumentEntity);
        }
    }

    protected void validateInstrument4Inactivation(InstrumentEntity oldInstrumentEntity) {
        throw new MFException(MFMsgKey.NO_VALID_INSTRUMENT_FOR_DEACTIVATION, "instrument with id:"+instrumentId + " not deactivated. Instruments with type:"+ oldInstrumentEntity.getInstrumentType() + " can not deactivated");
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected void setBusinesskey() {
        if(isNewInstrument) {
            if(businesskey==null) {
                this.businesskey = description.trim();
            }
            this.businesskey = this.businesskey.replace(" ", "");
            if(this.businesskey.length()> MAX_BUSINESSKEY_SIZE) this.businesskey = this.businesskey.substring(0, MAX_BUSINESSKEY_SIZE);
            this.businesskey = this.businesskey+"@"+getInstrumentType().getValue();
        }
    }

    public void setTreeLastChanged(LocalDateTime ts){
        this.ts = ts;
    }

    public String getInstrumentId() {
        return this.instrumentId;
    }
    public void setInstrumentId(String instrumentId) {
        initialized = true;
        this.instrumentId = instrumentId;
    }

    public Mono<InstrumentEntity> getSavedDomainObject() {
        return domainObjectMono;
    }

    abstract protected InstrumentEntity createDomainObject();
    abstract protected InstrumentType getInstrumentType();
}