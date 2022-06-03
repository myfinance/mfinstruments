package de.hf.myfinance.instruments.persistence.repositories;

import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentGraphEntry;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface InstrumentGraphRepository extends ReactiveCrudRepository<InstrumentGraphEntry, String> {

    Flux<InstrumentGraphEntry> findByDescendantAndEdgetype(String instrumentId, EdgeType edgeType);

    Flux<InstrumentGraphEntry> findByAncestorAndEdgetype(String instrumentId, EdgeType edgeType);
}
