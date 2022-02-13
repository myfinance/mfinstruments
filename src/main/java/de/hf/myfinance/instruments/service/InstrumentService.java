package de.hf.myfinance.instruments.service;

import de.hf.myfinance.instruments.persistence.TenantEntity;
import de.hf.myfinance.instruments.persistence.TenantRepository;
import de.hf.myfinance.restmodel.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InstrumentService {
    private final TenantMapper tenantMapper;
    private final TenantRepository tenantRepository;

    @Autowired
    public InstrumentService(TenantMapper tenantMapper, TenantRepository tenantRepository){
        this.tenantMapper = tenantMapper;
        this.tenantRepository = tenantRepository;
    }

    public void saveTenant(Tenant tenant) {
        TenantEntity tenantEntity = tenantMapper.apiToEntity(tenant);

        var newEntity = tenantRepository.save(tenantEntity);
    }

}
