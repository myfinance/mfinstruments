package de.hf.myfinance.instruments.events.in;

import de.hf.myfinance.event.Event;
import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentGraphEntry;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Configuration
public class SaveInstrumentTreeProcessorConfig {
    private static final Logger LOG = LoggerFactory.getLogger(SaveInstrumentProcessorConfig.class);

    private final InstrumentGraphRepository instrumentGraphRepository;

    @Autowired
    public SaveInstrumentTreeProcessorConfig(InstrumentGraphRepository instrumentGraphRepository) {
        this.instrumentGraphRepository = instrumentGraphRepository;
    }

    @Bean
    public Consumer<Event<String, Instrument>> saveInstrumentTreeProcessor() {
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            switch (event.getEventType()) {

                case CREATE:
                    Instrument instrument = event.getData();
                    LOG.info("Create instrument with ID: {}", instrument.getBusinesskey());
                    if(instrument.getInstrumentType().equals(InstrumentType.TENANT)) {
                        instrument.setParentBusinesskey(instrument.getBusinesskey());
                    }
                    if(instrument.getParentBusinesskey()!=null && !instrument.getParentBusinesskey().isEmpty()) {
                        var existingEntries = instrumentGraphRepository.findByDescendantAndEdgetype(instrument.getBusinesskey(), EdgeType.TENANTGRAPH).collectList().block();
                        if( existingEntries == null || existingEntries.isEmpty()) {
                            addInstrumentToGraph(instrument.getBusinesskey(), instrument.getParentBusinesskey(), EdgeType.TENANTGRAPH).block();
                        }
                    }
                    break;

                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE event";
                    LOG.warn(errorMessage);
            }

            LOG.info("Message processing done!");

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
        if(instrumentId!=ancestorId){
            final InstrumentGraphEntry newEntry = new InstrumentGraphEntry(ancestorId, ancestorId, edgeType);
            return Flux.just(newEntry);
        }
        return Flux.empty();
    }
}