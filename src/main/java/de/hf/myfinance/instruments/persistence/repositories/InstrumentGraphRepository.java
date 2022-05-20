package de.hf.myfinance.instruments.persistence.repositories;

import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentGraphEntry;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.List;

public interface InstrumentGraphRepository extends ReactiveCrudRepository<InstrumentGraphEntry, String> {

    List<InstrumentGraphEntry> findByDescendantAndEdgetype(String instrumentId, EdgeType edgeType);

    List<InstrumentGraphEntry> findByAncestorAndEdgetype(String instrumentId, EdgeType edgeType);
}
