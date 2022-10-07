package de.hf.myfinance.instruments.service.instrumentgraphhandler;


import de.hf.myfinance.instruments.persistence.DataReader;
import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class InstrumentGraphHandlerImpl implements InstrumentGraphHandler{

    final DataReader dataReader;

    public InstrumentGraphHandlerImpl(final InstrumentEnvironment instrumentEnvironment) {
        this.dataReader = instrumentEnvironment.getDataReader();
    }

    @Override
    public Mono<String> getRootInstrument(final String instrumentId, final EdgeType edgeType) {
        return dataReader.getRootInstrument(instrumentId, edgeType);
    }

    @Override
    public Flux<String> getInstrumentChildIds(final String instrumentId, final EdgeType edgeType){
        return getInstrumentChildIds(instrumentId, edgeType, 0);
    }

    @Override
    public  Flux<String> getInstrumentChildIds(final String instrumentId, final EdgeType edgeType, int pathlength){
        return dataReader.getInstrumentChildIds(instrumentId, edgeType, pathlength);
    }

    @Override
    public Flux<String> getAncestorIds(final String instrumentId, final EdgeType edgeType) {
        return dataReader.getAncestorIds(instrumentId, edgeType);
    }

    @Override
    public Mono<String> getRootInstrument(final String instrumentId) {
        return getRootInstrument(instrumentId, EdgeType.TENANTGRAPH);
    }
}