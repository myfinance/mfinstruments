package de.hf.myfinance.instruments.service;

import de.hf.myfinance.instruments.persistence.TenantEntity;
import de.hf.myfinance.restmodel.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TenantMapper {
    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Tenant entityToApi(TenantEntity entity);

    @Mappings({
            @Mapping(target = "instrumentid", ignore = true)
    })
    TenantEntity apiToEntity(Tenant api);

    List<Tenant> entityListToApiList(List<TenantEntity> entity);

    List<TenantEntity> apiListToEntityList(List<Tenant> api);
}
