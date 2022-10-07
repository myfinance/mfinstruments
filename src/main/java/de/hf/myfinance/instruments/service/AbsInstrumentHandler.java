package de.hf.myfinance.instruments.service;

import java.time.LocalDateTime;

import de.hf.framework.audit.AuditService;
import de.hf.framework.audit.Severity;
import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.events.out.EventHandler;
import de.hf.myfinance.instruments.persistence.DataReader;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import reactor.core.publisher.Mono;

/**
 * Base class for alle instrument handler
 */
public abstract class AbsInstrumentHandler {
    protected final DataReader dataReader;
    private final AuditService auditService;
    protected String instrumentId;
    protected boolean initialized = false;
    protected boolean exists = true;
    protected boolean isPropertyInit = false;
    protected LocalDateTime ts;
    protected static final String AUDIT_MSG_TYPE="InstrumentHandler_User_Event";
    protected String domainObjectName;
    protected String description = "";
    protected String businesskey = "";
    protected String oldDesc;
    protected boolean isActive = true;
    protected boolean isNewInstrument;
    private final EventHandler eventHandler;
    protected static final int MAX_BUSINESSKEY_SIZE = 32;

    protected AbsInstrumentHandler(InstrumentEnvironment instrumentEnvironment, String description, String businesskey, boolean isNewInstrument) {
        this.dataReader = instrumentEnvironment.getDataReader();
        this.auditService = instrumentEnvironment.getAuditService();
        ts = LocalDateTime.now();
        this.description = description;
        this.businesskey = businesskey;
        this.isNewInstrument = isNewInstrument;
        this.eventHandler = instrumentEnvironment.getEventHandler();
        setBusinesskey();
    }

    public Mono<Instrument> loadInstrument() {
        return this.dataReader.findByBusinesskey(businesskey)
                .switchIfEmpty(handleNotExistingInstrument(isNewInstrument))
                .map(e -> {
                    validateInstrument(e, getInstrumentType(), "");
                    return e;
                });
    }

    private Mono<Instrument> handleNotExistingInstrument(boolean isNewInstrument){
        if(isNewInstrument) {
            return Mono.just(initNewDomainObject());
        } else {
            return Mono.error(new MFException(MFMsgKey.UNKNOWN_INSTRUMENT_EXCEPTION, "Instrument for businesskey:"+businesskey + " does not exists."));
        }
    }

    protected Instrument initNewDomainObject() {
        var object = createDomainObject();
        object.setBusinesskey(this.businesskey);
        exists = false;
        return object;
    }

    protected void validateInstrument(Instrument instrument, InstrumentType instrumentType, String errMsg) {
        if(instrument.getInstrumentType()!=instrumentType){
            throw new MFException(MFMsgKey.WRONG_INSTRUMENTTYPE_EXCEPTION, errMsg+" instrument has wrong type:"+instrument.getInstrumentType());
        }
    }

    public Mono<String> save() {
        return loadInstrument().flatMap(this::saveOrUpdate);
    }

    private Mono<String> saveOrUpdate(Instrument instrument) {
        if(exists) {
            return updateInstrument(instrument);
        } else {
            exists = true;
            return saveNewInstrument(instrument);
        }
    }

    protected Mono<String> saveNewInstrument(Instrument instrument) {
        var newInstrument = setAdditionalValues(instrument);
        auditService.saveMessage(domainObjectName+" inserted: businesskey=" + newInstrument.getBusinesskey() + " desc=" + newInstrument.getDescription(), Severity.INFO, AUDIT_MSG_TYPE);
        eventHandler.sendInstrumentUpdatedEvent(newInstrument);
        return Mono.just("new Instrument with businesskey=" + newInstrument.getBusinesskey() +"approved");
    }

    protected Mono<String> updateInstrument(Instrument instrument) {
        checkInstrumentInactivation(instrument, isActive);
        oldDesc = instrument.getDescription();
        if (description != null && !description.equals("")) {
            instrument.setDescription(description);
        }
        var newInstrument = setAdditionalValues(instrument);
        auditService.saveMessage(domainObjectName + " updated:businesskey=" + newInstrument.getBusinesskey() + " desc=" + newInstrument.getDescription(), Severity.INFO, AUDIT_MSG_TYPE);
        eventHandler.sendInstrumentUpdatedEvent(newInstrument);
        return Mono.just("Instrument update with businesskey=" + newInstrument.getBusinesskey() +"approved");
    }

        protected Mono<Instrument> getInstrumentById(String instrumentId, String errMsg) {
            return dataReader.findById(instrumentId)
                .switchIfEmpty(
                    Mono.error(new MFException(MFMsgKey.UNKNOWN_INSTRUMENT_EXCEPTION, errMsg + " Instrument for id:" + instrumentId + " not found")));
    }

    protected Instrument setAdditionalValues(Instrument instrument) {
        return instrument;
    }

    protected void checkInstrumentInactivation(Instrument oldInstrument,  boolean isActiveAfterUpdate) {
        // try to deactivate instrument ?

        if(!isActiveAfterUpdate && oldInstrument.isIsactive()) {
            validateInstrument4Inactivation(oldInstrument);
        }
    }

    protected void validateInstrument4Inactivation(Instrument oldInstrument) {
        throw new MFException(MFMsgKey.NO_VALID_INSTRUMENT_FOR_DEACTIVATION, "instrument with id:"+instrumentId + " not deactivated. Instruments with type:"+ oldInstrument.getInstrumentType() + " can not deactivated");
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

    public void setValues(Instrument instrument){
        if(!isNewInstrument) {
            isActive = instrument.isIsactive();
        }
    }

    protected abstract Instrument createDomainObject();
    protected abstract InstrumentType getInstrumentType();
}