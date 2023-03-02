package de.hf.myfinance.instruments.events.in;

import de.hf.framework.audit.AuditService;
import de.hf.framework.audit.Severity;
import de.hf.myfinance.event.Event;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.persistence.InstrumentMapper;
import de.hf.myfinance.restmodel.Instrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Configuration
public class SaveInstrumentProcessorConfig {

    private final InstrumentMapper instrumentMapper;
    private final AuditService auditService;
    private final InstrumentRepository instrumentRepository;
    protected static final String AUDIT_MSG_TYPE="SaveInstrumentProcessor_Event";

    @Autowired
    public SaveInstrumentProcessorConfig(InstrumentMapper instrumentMapper, AuditService auditService, InstrumentRepository instrumentRepository) {
        this.instrumentMapper = instrumentMapper;
        this.auditService = auditService;
        this.instrumentRepository = instrumentRepository;
    }

    @Bean
    public Consumer<Event<String, Instrument>> saveInstrumentProcessor() {
        return event -> {
            auditService.saveMessage("Process message created at " + event.getEventCreatedAt(), Severity.INFO, AUDIT_MSG_TYPE);

            switch (event.getEventType()) {

                case CREATE:
                    Instrument instrument = event.getData();
                    auditService.saveMessage("Create instrument with ID: " + instrument.getBusinesskey(), Severity.INFO, AUDIT_MSG_TYPE);
                    var instrumentEntity = instrumentMapper.apiToEntity(instrument);
                    instrumentRepository.findByBusinesskey(instrumentEntity.getBusinesskey())
                            .switchIfEmpty(Mono.just(instrumentEntity))
                            .map(e -> {
                                e.setAdditionalMaps(instrumentEntity.getAdditionalMaps());
                                e.setAdditionalProperties(instrumentEntity.getAdditionalProperties());
                                e.setDescription(instrumentEntity.getDescription());
                                e.setActive(instrumentEntity.isActive());
                                e.setTreelastchanged(instrumentEntity.getTreelastchanged());
                                return e;
                            })
                            .flatMap(e -> instrumentRepository.save(e))
                            .block();
                    auditService.saveMessage("Instrument updated:businesskey=" + instrument.getBusinesskey() + " desc=" + instrument.getDescription(), Severity.INFO, AUDIT_MSG_TYPE);

                    break;

                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE event";
                    auditService.saveMessage(errorMessage, Severity.WARN, AUDIT_MSG_TYPE);
            }

            auditService.saveMessage("Message processing done!", Severity.INFO, AUDIT_MSG_TYPE);

        };
    }
}
