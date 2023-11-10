package de.hf.myfinance.instruments.service;

import java.time.LocalDateTime;

import de.hf.framework.audit.AuditService;
import de.hf.framework.audit.Severity;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.events.out.InstrumentApprovedEventHandler;
import de.hf.myfinance.instruments.persistence.DataReader;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import reactor.core.publisher.Mono;

/**
 * Base class for alle instrument handler
 */
public abstract class AbsInstrumentHandler implements InstrumentHandler{
    protected final DataReader dataReader;
    protected final AuditService auditService;
    protected final Instrument requestedInstrument;
    protected String businesskey = "";
    protected boolean isNewInstrument = false;

    protected String instrumentId;
    protected boolean exists = true;
    protected LocalDateTime ts;
    protected static final String AUDIT_MSG_TYPE="InstrumentHandler_User_Event";

    protected boolean isSimpleValidation = false;


    private final InstrumentApprovedEventHandler eventHandler;
    protected static final int MAX_BUSINESSKEY_SIZE = 32;

    protected AbsInstrumentHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        this.dataReader = instrumentEnvironment.getDataReader();
        this.auditService = instrumentEnvironment.getAuditService();
        ts = LocalDateTime.now();
        this.eventHandler = instrumentEnvironment.getEventHandler();
        this.requestedInstrument = instrument;
        setBusinesskey();
    }

    private void setBusinesskey() {
        this.businesskey = requestedInstrument.getBusinesskey();
        if(this.businesskey==null || this.businesskey.isEmpty()) {
            this.businesskey = initBusinesskey().replace(" ", "").trim();
            if(this.businesskey.length()> MAX_BUSINESSKEY_SIZE) this.businesskey = this.businesskey.substring(0, MAX_BUSINESSKEY_SIZE);
            this.businesskey = this.businesskey+"@"+getInstrumentType().getValue();
            isNewInstrument = true;
        } else {
            isNewInstrument = false;
        }
    }

    protected String initBusinesskey() {
        if(requestedInstrument.getDescription()==null || requestedInstrument.getDescription().isEmpty()){
            auditService.throwException("wether this businesskey nor the description is defined for the instrument", AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT);
        }
        return requestedInstrument.getDescription();
    }

    public Mono<Instrument> loadInstrument() {
        return this.dataReader.findByBusinesskey(businesskey)
                .switchIfEmpty(handleNotExistingInstrument())
                .map(e -> {
                    validateLoadedInstrument(e, getInstrumentType(), "");
                    return e;
                });
    }

    private Mono<Instrument> handleNotExistingInstrument(){
        if(isNewInstrument) {
            exists = false;
            return Mono.just(initNewDomainObject());
        } else {
            return auditService.handleMonoError("Instrument for businesskey:"+businesskey + " does not exists.", AUDIT_MSG_TYPE, MFMsgKey.UNKNOWN_INSTRUMENT_EXCEPTION).cast(Instrument.class);
        }
    }

    protected Instrument initNewDomainObject() {
        var object = createDomainObject();
        object.setBusinesskey(this.businesskey);
        exists = false;
        return object;
    }

    protected void validateLoadedInstrument(Instrument instrument, InstrumentType instrumentType, String errMsg) {
        if(instrument.getInstrumentType()!=instrumentType){
            auditService.throwException(errMsg+" instrument has wrong type:"+instrument.getInstrumentType(), AUDIT_MSG_TYPE, MFMsgKey.WRONG_INSTRUMENTTYPE_EXCEPTION);
        }
        if(isNewInstrument && exists){
            auditService.throwException(errMsg+" you try to insert a new instrument with description :"+requestedInstrument.getDescription() + ", but the generated businesskey allready exists:" + instrument.getBusinesskey(), AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT);
        }
    }

    public Mono<String> save() {
        return loadInstrument()
                .flatMap(this::setBasicValues)
                .flatMap(this::setAdditionalValues)
                .flatMap(this::validateIsActive)
                .flatMap(this::validateInstrument)
                .flatMap(this::instrumentApproved)
                .flatMap(this::postApproveAction);
    }

    protected Mono<String> postApproveAction(String msg){
        return Mono.just("post approve action done");
    }

    private Mono<String> instrumentApproved(Instrument validatedInstrument) {
        auditService.saveMessage("Instrument validated:businesskey=" + validatedInstrument.getBusinesskey() + " desc=" + validatedInstrument.getDescription(), Severity.INFO, AUDIT_MSG_TYPE);
        eventHandler.sendInstrumentApprovedEvent(validatedInstrument);
        return Mono.just("Instrument update with businesskey=" + validatedInstrument.getBusinesskey() +"approved");
    }

    protected Mono<Instrument> setBasicValues(Instrument validatedInstrument) {
        if (requestedInstrument.getDescription() != null && !requestedInstrument.getDescription().isEmpty()) {
            validatedInstrument.setDescription(requestedInstrument.getDescription());
        }
        validatedInstrument.setTreelastchanged(ts);
        return Mono.just(validatedInstrument);
    }

    private Mono<Instrument> validateIsActive(Instrument instrument) {
        if(!requestedInstrument.isActive() && !instrument.isActive()) {
            return auditService.handleMonoError("you can not change inactive instruments", AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT).cast(Instrument.class);
        }
        if(!requestedInstrument.isActive() && instrument.isActive()) {
            return validateInstrument4Inactivation(instrument);
        }
        instrument.setActive(requestedInstrument.isActive());
        return Mono.just(instrument);
    }

    //the default allowes to inactivate the instrument
    protected Mono<Instrument> validateInstrument4Inactivation(Instrument instrument) {
        instrument.setActive(false);
        return Mono.just(instrument);
    }

    protected Mono<Instrument> setAdditionalValues(Instrument instrument) {
        return Mono.just(instrument);
    }

    protected Mono<Instrument> getInstrumentById(String instrumentId, String errMsg) {
        return dataReader.findById(instrumentId)
                .switchIfEmpty(
                        auditService.handleMonoError(errMsg + " Instrument for id:" + instrumentId + " not found", AUDIT_MSG_TYPE, MFMsgKey.UNKNOWN_INSTRUMENT_EXCEPTION).cast(Instrument.class)
                        );
    }

    protected Mono<Instrument> validateInstrument(Instrument instrument){
        return Mono.just(instrument);
    }

    public void setTreeLastChanged(LocalDateTime ts){
        this.ts = ts;
    }

    public void setIsSimpleValidation(boolean isSimpleValidation) {
        this.isSimpleValidation = isSimpleValidation;
    }

    protected abstract Instrument createDomainObject();
    protected abstract InstrumentType getInstrumentType();
}