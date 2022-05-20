package de.hf.myfinance.instruments.service;

import com.google.common.collect.Lists;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class InstrumentService {
    private final InstrumentMapper instrumentMapper;
    private final InstrumentRepository instrumentRepository;
    private final InstrumentFactory instrumentFactory;

    @Autowired
    public InstrumentService(InstrumentMapper instrumentMapper, InstrumentRepository instrumentRepository, InstrumentFactory instrumentFactory){
        this.instrumentMapper = instrumentMapper;
        this.instrumentRepository = instrumentRepository;
        this.instrumentFactory = instrumentFactory;
    }

    public Mono<Instrument> getInstrument(String businesskey) {
        var instrumentHandler = instrumentFactory.getInstrumentHandlerForExistingInstrument(businesskey);
        return instrumentHandler.getSavedDomainObject()
                .map(e-> instrumentMapper.entityToApi(e));
    }

    public void newTenant(String description) {
        var tenantHandler = instrumentFactory.getInstrumentHandlerForNewInstrument(InstrumentType.TENANT, description, null);
        tenantHandler.save();
    }

    public List<Instrument> listInstruments() {
        return instrumentMapper.entityListToApiList(Lists.newArrayList(instrumentFactory.listInstruments()));
    }

    public List<Instrument> listTenants(){
        return instrumentMapper.entityListToApiList(instrumentFactory.listTenants());
    }

    public List<Instrument> listInstruments(String tenantkey){
        return instrumentMapper.entityListToApiList(Lists.newArrayList(instrumentFactory.getTenantHandler(tenantkey).listInstruments()));
    }

    public void updateInstrument(String businesskey, String description, boolean isActive) {
        var instrumentHandler = instrumentFactory.getInstrumentHandlerForExistingInstrument(businesskey);
        instrumentHandler.setActive(isActive);
        instrumentHandler.setDescription(description);
        instrumentHandler.save();
    }
}
