package de.hf.myfinance.instruments.persistence.repositories;

import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.entities.InstrumentGraphEntry;
import de.hf.myfinance.instruments.persistence.entities.InstrumentGraphId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InstrumentGraphRepository extends CrudRepository<InstrumentGraphEntry, InstrumentGraphId> {
    @Query("select i from InstrumentGraphEntry i where i.id.descendant = ?1 and i.id.edgetype = ?2")
    List<InstrumentGraphEntry> findByDescendantAndEdgetype(int instrumentId, EdgeType edgeType);

    @Query("select i.descendant from InstrumentGraphEntry i where i.id.ancestor = ?1 and i.id.edgetype = ?2 and i.pathlength= ?3")
    List<InstrumentEntity> getInstrumentChilds(int instrumentId, EdgeType edgeType, int pathlength);

    @Query("select i.descendant from InstrumentGraphEntry i where i.id.ancestor = ?1 and i.id.edgetype = ?2 and i.pathlength>0")
    List<InstrumentEntity> getInstrumentChilds(int instrumentId, EdgeType edgeType);
}
