package de.hf.myfinance.instruments.persistence;

import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.restmodel.Instrument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DataReader {
    Mono<Instrument> findByBusinesskey(String businesskey);
    Mono<Instrument> findById(String instrumentId);
    Flux<Instrument> findAllById(Iterable<String> ids);
    Flux<Instrument> findAll();
    Mono<String> getRootInstrument(final String instrumentId, final EdgeType edgeType);
    Flux<Instrument> findByBusinesskeyIn(Iterable<String> businesskeyIterable);
    Flux<String> getInstrumentChildIds(final String instrumentId, final EdgeType edgeType, int pathlength);
    Flux<String> getAncestorIds(final String businesskey, final EdgeType edgeType);
    Flux<Instrument> findActiveInstruments();
    Mono<Boolean> isInactivateable(String businesskey);
}
