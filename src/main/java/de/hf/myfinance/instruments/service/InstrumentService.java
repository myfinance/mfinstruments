package de.hf.myfinance.instruments.service;


import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class InstrumentService {
    private final InstrumentMapper instrumentMapper;
    private final InstrumentRepository instrumentRepository;
    private final InstrumentFactory instrumentFactory;
    InstrumentGraphRepository instrumentGraphRepository;

    @Autowired
    public InstrumentService(InstrumentMapper instrumentMapper, InstrumentRepository instrumentRepository, InstrumentFactory instrumentFactory, InstrumentGraphRepository instrumentGraphRepository){
        this.instrumentMapper = instrumentMapper;
        this.instrumentRepository = instrumentRepository;
        this.instrumentFactory = instrumentFactory;
        this.instrumentGraphRepository = instrumentGraphRepository;
    }

    public Mono<Instrument> getInstrument(String businesskey) {
        var instrumentHandler = instrumentFactory.getInstrumentHandlerForExistingInstrument(businesskey);
        return instrumentHandler.loadInstrument()
                .map(e-> instrumentMapper.entityToApi(e));
    }

    public Mono<Instrument> addInstrument(Instrument instrument) {
        Mono<InstrumentEntity> savedInstrument;
        var parentBusinesskey = instrument.getParentBusinesskey();
        if(instrument.getInstrumentType().equals(InstrumentType.TENANT)){
            parentBusinesskey = null;
        }
        var instrumentHandler = instrumentFactory.getInstrumentHandlerForNewInstrument(instrument.getInstrumentType(), instrument.getDescription(), parentBusinesskey);

        return instrumentHandler.save().map(e-> instrumentMapper.entityToApi(e));
    }

    public Flux<Instrument> listInstruments() {
        return instrumentFactory.listInstruments().map(e-> instrumentMapper.entityToApi(e));
    }

    public Flux<Instrument> listTenants(){
        return instrumentFactory.listTenants().map(e-> instrumentMapper.entityToApi(e));
    }

    public Flux<Instrument> listInstruments(String tenantkey){
        return instrumentFactory.getTenantHandler(tenantkey).listInstrumentChilds().map(e-> instrumentMapper.entityToApi(e));
    }

    public Flux<Instrument> listActiveInstruments(String tenantkey) {
        return instrumentFactory.getTenantHandler(tenantkey).listActiveInstrumentChilds().map(e-> instrumentMapper.entityToApi(e));
    }

    public Flux<Instrument> listInstrumentsByType(String tenantkey, InstrumentType instrumentType) {
        return instrumentFactory.getTenantHandler(tenantkey).listInstrumentChilds(instrumentType, true).map(e-> instrumentMapper.entityToApi(e));
    }
}
