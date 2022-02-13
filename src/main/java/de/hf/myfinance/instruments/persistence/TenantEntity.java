/**
 * ----------------------------------------------------------------------------
 * ---          HF - Application Development                       ---
 * Copyright (c) 2014, ... All Rights Reserved
 * Project     : dac
 * File        : Tenant.java
 * Author(s)   : hf
 * Created     : 20.12.2018
 * ----------------------------------------------------------------------------
 */

package de.hf.myfinance.instruments.persistence;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name="mf_instrument")
@PrimaryKeyJoinColumn(name="instrumentid")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(InstrumentType.TENANT_IDSTRING)
public class TenantEntity extends Instrument {

    public TenantEntity(){
        super();
    }

    public TenantEntity(String description, boolean isactive, LocalDateTime treelastchanged){
        super(InstrumentType.TENANT, description, isactive, treelastchanged);
    }
}
