package de.hf.myfinance.instruments.persistence.repositories;

import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;


public interface InstrumentRepository extends ReactiveCrudRepository<InstrumentEntity, String> {
    Mono<InstrumentEntity> findByBusinesskey(String businesskey);
}
