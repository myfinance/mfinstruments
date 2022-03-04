package de.hf.myfinance.instruments.persistence.repositories;

import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import org.springframework.data.repository.CrudRepository;

public interface InstrumentRepository extends CrudRepository<InstrumentEntity, Integer> {

}
