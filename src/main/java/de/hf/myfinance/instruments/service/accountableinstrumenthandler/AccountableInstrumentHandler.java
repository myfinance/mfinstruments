package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountableInstrumentHandler {
    Mono<String> getTenant();
    Flux<InstrumentEntity> getInstrumentChilds(EdgeType edgeType, int pathlength);
    Flux<String> getAncestorIds();
}