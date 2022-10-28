package de.hf.myfinance.instruments.persistence;

import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentGraphEntry;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.service.InstrumentMapper;
import de.hf.myfinance.restmodel.Instrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class DataReaderImpl implements DataReader{
    private final InstrumentRepository instrumentRepository;
    private final InstrumentGraphRepository instrumentGraphRepository;
    private final InstrumentMapper instrumentMapper;

    @Autowired
    public DataReaderImpl(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, InstrumentMapper instrumentMapper) {
        this.instrumentRepository = instrumentRepository;
        this.instrumentGraphRepository = instrumentGraphRepository;
        this.instrumentMapper = instrumentMapper;
    }

    @Override
    public Mono<Instrument> findByBusinesskey(String businesskey) {
        return instrumentRepository.findByBusinesskey(businesskey).map(e-> instrumentMapper.entityToApi(e));
    }

    @Override
    public Mono<Instrument> findById(String instrumentId) {
        return instrumentRepository.findById(instrumentId).map(e-> instrumentMapper.entityToApi(e));
    }

    @Override
    public Flux<Instrument> findAllById(Iterable<String> ids) {
        return instrumentRepository.findAllById(ids).map(e-> instrumentMapper.entityToApi(e));
    }

    @Override
    public Flux<Instrument> findByBusinesskeyIn(Iterable<String> businesskeyIterable) {
        return instrumentRepository.findByBusinesskeyIn(businesskeyIterable).map(e-> instrumentMapper.entityToApi(e));
    }

    @Override
    public Flux<Instrument> findAll() {
        return instrumentRepository.findAll().map(e-> instrumentMapper.entityToApi(e));
    }

    @Override
    public Mono<String> getRootInstrument(final String businessKey, final EdgeType edgeType) {
        return instrumentGraphRepository.findByDescendantAndEdgetype(businessKey, edgeType)
                .reduce((e1, e2) -> {
                    if(e1.getPathlength() >= e2.getPathlength()) {
                        return e1;
                    }
                    return e2;
                })
                .map(e->e.getAncestor());
    }

    @Override
    public Flux<String> getInstrumentChildIds(final String instrumentId, final EdgeType edgeType, int pathlength){
        var childs = instrumentGraphRepository.findByAncestorAndEdgetype(instrumentId, edgeType);
        if(pathlength>0) {
            childs =childs.filter(e->e.getPathlength()==pathlength);
        } else {
            childs = childs.filter(e->e.getPathlength()>pathlength);
        }
        return childs.map(e->e.getDescendant());
    }

    @Override
    public Flux<String> getAncestorIds(final String businesskey, final EdgeType edgeType) {
        return instrumentGraphRepository.findByDescendantAndEdgetype(businesskey, edgeType).map(InstrumentGraphEntry::getAncestor);
    }
}
