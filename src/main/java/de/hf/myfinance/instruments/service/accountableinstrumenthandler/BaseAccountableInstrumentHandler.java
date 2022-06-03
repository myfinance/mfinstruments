package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BaseAccountableInstrumentHandler {
    Flux<String> getAncestorIds();
    Flux<InstrumentEntity> getInstrumentChilds(EdgeType edgeType, int pathlength);
    Mono<String> getTenant();
    //List<InstrumentPropertiesEntity> getInstrumentProperties();
}
