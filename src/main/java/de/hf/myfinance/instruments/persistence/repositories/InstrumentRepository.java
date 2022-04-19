package de.hf.myfinance.instruments.persistence.repositories;

import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.entities.InstrumentGraphEntry;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InstrumentRepository extends CrudRepository<InstrumentEntity, String> {
    InstrumentEntity findByBusinesskey(String businesskey);
}
