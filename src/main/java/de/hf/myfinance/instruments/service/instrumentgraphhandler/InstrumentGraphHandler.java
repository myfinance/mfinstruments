package de.hf.myfinance.instruments.service.instrumentgraphhandler;

import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentGraphEntry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InstrumentGraphHandler {
    /**
     * add an instrument to the graph
     * @param instrumentId the instrumentId of the instrument
     * @param ancestorId the id of the parent of the instrument
     * @param edgeType the edgetype that describes the relation between parent and child e.G. TenantGraph
     */
    Mono<InstrumentGraphEntry> addInstrumentToGraph(String instrumentId, String ancestorId, EdgeType edgeType);
    /** calls addInstrumentToGraph(int instrumentId, int ancestorId, EdgeType edgeType) with EdgeType =  TenantGraph */
    Mono<InstrumentGraphEntry> addInstrumentToGraph(String instrumentId, String ancestorId);

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


    Flux<InstrumentGraphEntry> getAncestors(final String instrumentId, final EdgeType edgeType);
    
}