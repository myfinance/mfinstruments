package de.hf.myfinance.instruments.persistence.repositories;

import de.hf.myfinance.instruments.persistence.entities.InActivationInfoEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface InActivationInfoRepository extends ReactiveCrudRepository<InActivationInfoEntity, String> {
    Mono<InActivationInfoEntity> findByBusinesskey(String businesskey);
}
