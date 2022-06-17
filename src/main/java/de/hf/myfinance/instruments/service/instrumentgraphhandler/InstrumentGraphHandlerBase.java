package de.hf.myfinance.instruments.service.instrumentgraphhandler;


import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentGraphEntry;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithGraph;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class InstrumentGraphHandlerBase implements InstrumentGraphHandler{
    final InstrumentGraphRepository instrumentGraphRepository;

    public InstrumentGraphHandlerBase(InstrumentEnvironmentWithGraph instrumentEnvironment) {
            this.instrumentGraphRepository = instrumentEnvironment.getInstrumentGraphRepository();
    }

    @Override
    public Mono<InstrumentGraphEntry> addInstrumentToGraph(final String instrumentId, final String ancestorId, final EdgeType edgeType){

        return instrumentGraphRepository.findByDescendantAndEdgetype(ancestorId, edgeType)
                .switchIfEmpty(handleNotExistingEntry(instrumentId, ancestorId, edgeType))
                .map(e-> {
                    final InstrumentGraphEntry newEntry4EachExisting = new InstrumentGraphEntry(e.getAncestor(), instrumentId, edgeType);
                    newEntry4EachExisting.setPathlength(e.getPathlength()+1);
                    return newEntry4EachExisting;
                })
                .flatMap(this::save)
                .then(instrumentGraphRepository.save(new InstrumentGraphEntry(instrumentId, instrumentId, edgeType)));
    }

    private Mono<InstrumentGraphEntry> save(InstrumentGraphEntry instrumentGraphEntry) {
        return instrumentGraphRepository.save(instrumentGraphEntry);
    }


    private Flux<InstrumentGraphEntry> handleNotExistingEntry(final String instrumentId, final String ancestorId, final EdgeType edgeType){
        if(instrumentId!=ancestorId){
            final InstrumentGraphEntry newEntry = new InstrumentGraphEntry(ancestorId, ancestorId, edgeType);
            newEntry.setPathlength(0);
            return Flux.just(newEntry);
        }
        return Flux.empty();
    }

    @Override
    public Mono<String> getRootInstrument(final String instrumentId, final EdgeType edgeType) {
        return instrumentGraphRepository.findByDescendantAndEdgetype(instrumentId, edgeType)
                .reduce((e1, e2) -> {
                    if(e1.getPathlength() >= e2.getPathlength()) {
                        return e1;
                    }
                    return e2;
                })
                .map(e->e.getAncestor());
    }

    @Override
    public  Flux<String> getInstrumentChildIds(final String instrumentId, final EdgeType edgeType){
        return getInstrumentChildIds(instrumentId, edgeType, 0);
    }

    @Override
    public  Flux<String> getInstrumentChildIds(final String instrumentId, final EdgeType edgeType, int pathlength){
        var childs = instrumentGraphRepository.findByAncestorAndEdgetype(instrumentId, edgeType);
        if(pathlength>0) {
            childs =childs.filter(e->e.getPathlength()==pathlength);
        } else {
            childs = childs.filter(e->e.getPathlength()>pathlength);
        }
        return childs.map(e->e.getDescendant());
    }

    @Override
    public Flux<InstrumentGraphEntry> getAncestors(final String instrumentId, final EdgeType edgeType) {
        return  instrumentGraphRepository.findByDescendantAndEdgetype(instrumentId, edgeType);
    }
}