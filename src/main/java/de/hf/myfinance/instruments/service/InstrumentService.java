package de.hf.myfinance.instruments.service;

import com.google.common.collect.Lists;
import de.hf.framework.audit.Severity;
import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.entities.InstrumentGraphEntry;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentImpl;
import de.hf.myfinance.instruments.service.instrumentgraphhandler.InstrumentGraphHandlerImpl;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

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
        if(instrument.getInstrumentType().equals(InstrumentType.TENANT)) {
            savedInstrument = newTenant(instrument.getDescription());
        } else {
            throw new MFException(MFMsgKey.WRONG_INSTRUMENTTYPE_EXCEPTION, "instrument with description:"+instrument.getDescription() + " not saved. Instrumenttype:"+ instrument.getInstrumentType() + " unknown");
        }
        return savedInstrument.map(e-> instrumentMapper.entityToApi(e));
    }

    protected Mono<InstrumentEntity> newTenant(String description) {
        var tenantHandler = instrumentFactory.getInstrumentHandlerForNewInstrument(InstrumentType.TENANT, description, null);
        return tenantHandler.save();
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
}
