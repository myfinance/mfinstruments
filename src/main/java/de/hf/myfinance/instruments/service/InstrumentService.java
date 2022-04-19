package de.hf.myfinance.instruments.service;

import com.google.common.collect.Lists;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import de.hf.myfinance.restmodel.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InstrumentService {
    private final InstrumentMapper instrumentMapper;
    private final InstrumentRepository instrumentRepository;
    private final InstrumentFactory instrumentFactory;

    @Autowired
    public InstrumentService(InstrumentMapper instrumentMapper, InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, InstrumentFactory instrumentFactory){
        this.instrumentMapper = instrumentMapper;
        this.instrumentRepository = instrumentRepository;
        this.instrumentFactory = instrumentFactory;
    }

    public void saveTenant(Tenant tenant) {
        var instrumentEntity = instrumentMapper.apiToEntity(tenant);

        var newEntity = instrumentRepository.save(instrumentEntity);
    }

    public void newTenant(String description) {
        var tenantHandler = instrumentFactory.getInstrumentHandler(InstrumentType.TENANT, description, null, null);
        tenantHandler.save();
    }

    public List<Instrument> listInstruments() {
        return instrumentMapper.entityListToApiList(Lists.newArrayList(instrumentFactory.listInstruments()));
    }

    public List<Instrument> listTenants(){
        return instrumentMapper.entityListToApiList(instrumentFactory.listTenants());
    }

    public List<Instrument> listInstruments(String tenantkey){
        return instrumentMapper.entityListToApiList(Lists.newArrayList(instrumentFactory.getTenantHandler(tenantkey, false).listInstruments()));
    }
}
