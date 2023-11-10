package de.hf.myfinance.instruments.events.in;

import de.hf.framework.audit.AuditService;
import de.hf.framework.audit.Severity;
import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.event.Event;
import de.hf.myfinance.instruments.service.InstrumentService;
import de.hf.myfinance.restmodel.Instrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.function.Consumer;

@Configuration
public class ValidateInstrumentProcessorConfig {

    private final InstrumentService instrumentService;
    private final AuditService auditService;
    protected static final String AUDIT_MSG_TYPE="ValidateInstrumentProcessor_Event";

    @Autowired
    public ValidateInstrumentProcessorConfig(InstrumentService instrumentService, AuditService auditService) {
        this.instrumentService = instrumentService;
        this.auditService = auditService;
    }

    @Bean
    public Consumer<Event<String, Instrument>> validateInstrumentProcessor() {
        return event -> {
            auditService.saveMessage("Process message created at "+ event.getEventCreatedAt(), Severity.DEBUG, AUDIT_MSG_TYPE);
            switch (event.getEventType()) {

                case CREATE:
                    Instrument instrument = event.getData();
                    auditService.saveMessage("Create instrument with ID:"+ instrument.getBusinesskey(), Severity.DEBUG, AUDIT_MSG_TYPE);
                    try{
                        instrumentService.saveInstrument(instrument).block();
                    }catch(MFException e){
                        //no need to throw mfExceptions. These are Validation-Errors and retry the message makes no sense.  
                    }
                    catch(Exception e){
                        auditService.saveMessage("unexpected error", Severity.FATAL, AUDIT_MSG_TYPE);
                        throw e;
                    }
                    break;

                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE event";
                    auditService.saveMessage(errorMessage, Severity.FATAL, AUDIT_MSG_TYPE);
            }
            auditService.saveMessage("Message processing done!", Severity.DEBUG, AUDIT_MSG_TYPE);

        };
    }
}