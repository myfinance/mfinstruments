package de.hf.myfinance.instruments.persistence;

import org.springframework.data.repository.CrudRepository;

public interface  TenantRepository extends CrudRepository<TenantEntity, Integer> {

}
