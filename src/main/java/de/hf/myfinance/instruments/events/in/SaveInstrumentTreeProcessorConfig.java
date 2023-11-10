package de.hf.myfinance.instruments.events.in;

import de.hf.framework.audit.AuditService;
import de.hf.framework.audit.Severity;
import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.event.Event;
import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentGraphEntry;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Configuration
public class SaveInstrumentTreeProcessorConfig {

    private final InstrumentGraphRepository instrumentGraphRepository;
    private final AuditService auditService;
    protected static final String AUDIT_MSG_TYPE="ValidateInstrumentTreeProcessor_Event";

    @Autowired
    public SaveInstrumentTreeProcessorConfig(InstrumentGraphRepository instrumentGraphRepository, AuditService auditService) {
        this.instrumentGraphRepository = instrumentGraphRepository;
        this.auditService = auditService;
    }

    @Bean
    public Consumer<Event<String, Instrument>> saveInstrumentTreeProcessor() {
        return event -> {
            auditService.saveMessage("Process message created at "+ event.getEventCreatedAt(), Severity.DEBUG, AUDIT_MSG_TYPE);

            switch (event.getEventType()) {

                case CREATE:
                    try{
                        Instrument instrument = event.getData();
                        auditService.saveMessage("Create instrument with ID:"+ instrument.getBusinesskey(), Severity.DEBUG, AUDIT_MSG_TYPE);
                        if(instrument.getInstrumentType().equals(InstrumentType.TENANT)) {
                            instrument.setParentBusinesskey(instrument.getBusinesskey());
                        }
                        if(instrument.getParentBusinesskey()!=null && !instrument.getParentBusinesskey().isEmpty()) {
                            var existingEntries = instrumentGraphRepository.findByDescendantAndEdgetype(instrument.getBusinesskey(), EdgeType.TENANTGRAPH).collectList().block();
                            if( existingEntries == null || existingEntries.isEmpty()) {
                                addInstrumentToGraph(instrument.getBusinesskey(), instrument.getParentBusinesskey(), EdgeType.TENANTGRAPH).block();
                            }
                        }
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

    private Mono<InstrumentGraphEntry> addInstrumentToGraph(final String instrumentId, final String ancestorId, final EdgeType edgeType){

        return instrumentGraphRepository.findByDescendantAndEdgetype(ancestorId, edgeType)
                .switchIfEmpty(handleNotExistingEntry(instrumentId, ancestorId, edgeType))
                .map(e-> {
                    final InstrumentGraphEntry newEntry4EachExisting = new InstrumentGraphEntry(e.getAncestor(), instrumentId, edgeType);
                    newEntry4EachExisting.setPathlength(e.getPathlength()+1);
                    return newEntry4EachExisting;
                })
                .flatMap(this::save)
                .then(instrumentGraphRepository.save(new InstrumentGraphEntry(instrumentId, instrumentId, edgeType)));
    }

    private Mono<InstrumentGraphEntry> save(InstrumentGraphEntry instrumentGraphEntry) {
        return instrumentGraphRepository.save(instrumentGraphEntry);
    }

    private Flux<InstrumentGraphEntry> handleNotExistingEntry(final String instrumentId, final String ancestorId, final EdgeType edgeType){
        if(!instrumentId.equals(ancestorId)){
            final InstrumentGraphEntry newEntry = new InstrumentGraphEntry(ancestorId, ancestorId, edgeType);
            return Flux.just(newEntry);
        }
        return Flux.empty();
    }
}