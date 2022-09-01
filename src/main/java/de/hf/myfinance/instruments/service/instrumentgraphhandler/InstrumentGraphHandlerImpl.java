package de.hf.myfinance.instruments.service.instrumentgraphhandler;


import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentGraphEntry;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithGraph;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class InstrumentGraphHandlerImpl extends InstrumentGraphHandlerBase{

    public InstrumentGraphHandlerImpl(final InstrumentEnvironmentWithGraph instrumentEnvironment) {
        super(instrumentEnvironment);
    }

    @Override
    public Mono<InstrumentGraphEntry> addInstrumentToGraph(final String instrumentId, final String ancestorId){
        return addInstrumentToGraph(instrumentId, ancestorId, EdgeType.TENANTGRAPH);
    }

    @Override
    public Mono<String> getRootInstrument(final String instrumentId) {
        return getRootInstrument(instrumentId, EdgeType.TENANTGRAPH);
    }

}