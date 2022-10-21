package de.hf.myfinance.instruments.service.instrumentgraphhandler;

import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InstrumentGraphHandler {

    /**
     * get the id of the rootinstrument in the graph 
     * @param instrumentId the id of the child instrument where there the root is requested for
     * @param edgeType the edgetype that describes the relation between parent and child e.G. TenantGraph
     * @return the optional of the id of the root instrument
     */
    Mono<String> getRootInstrument(String instrumentId, EdgeType edgeType);
    /** calls getRootInstrument(int instrumentId, EdgeType edgeType) with EdgeType =  TenantGraph */
    Mono<String> getRootInstrument(String instrumentId);

    Flux<String> getInstrumentChildIds(final String instrumentId, final EdgeType edgeType);
    Flux<String> getInstrumentChildIds(final String instrumentId, final EdgeType edgeType, int pathlength);


    Flux<String> getAncestorIds(final String instrumentId, final EdgeType edgeType);
    
}