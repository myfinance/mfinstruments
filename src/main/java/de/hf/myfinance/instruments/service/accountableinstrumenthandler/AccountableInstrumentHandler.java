package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.service.InstrumentHandler;
import de.hf.myfinance.restmodel.Instrument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountableInstrumentHandler extends InstrumentHandler {
    Mono<String> getTenant();
    Flux<Instrument> getInstrumentChilds(EdgeType edgeType, int pathlength);
    Flux<String> getAncestorIds();
    void setTenant(String tenantBusinesskey);
}