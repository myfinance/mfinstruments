/** ----------------------------------------------------------------------------
 *
 * ---          HF - Application Development                       ---
 *              Copyright (c) 2013, ... All Rights Reserved
 *
 *
 *  Project     : domain
 *
 *  File        : Instrument.java
 *
 *  Author(s)   : hf
 *
 *  Created     : 16.12.2013
 *
 * ----------------------------------------------------------------------------
 */
 package de.hf.myfinance.instruments.persistence.entities;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


import de.hf.myfinance.restmodel.AdditionalMaps;
import de.hf.myfinance.restmodel.AdditionalProperties;
import de.hf.myfinance.restmodel.InstrumentType;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;



@Document(collection = "instruments")
public class InstrumentEntity implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String instrumentid;
    @Version
    private Integer version;
    private Integer instrumentTypeId;
    private InstrumentType instrumentType;
    private String description;
    private boolean active;
    private LocalDateTime treelastchanged;
    @Indexed(unique = true)
    private String businesskey;
    private String parentBusinesskey;
    private String tenantBusinesskey;

    private Map<AdditionalMaps, Map<String, String>> additionalMaps = new HashMap<>();
    private Map<AdditionalProperties, String> additionalProperties = new HashMap<>();

    public InstrumentEntity() {
    }

    public InstrumentEntity(InstrumentType instrumentType, String description, boolean active, LocalDateTime treelastchanged) {
        setInstrumentTypeId(instrumentType.getValue());
        this.description = description;
        this.active = active;
        this.treelastchanged = treelastchanged;
    }

    public String getInstrumentid() {
        return this.instrumentid;
    }
    public void setInstrumentid(String instrumentid) {
        this.instrumentid = instrumentid;
    }

    protected Integer getInstrumentTypeId() {
        return this.instrumentTypeId;
    }
    protected void setInstrumentTypeId(Integer instrumentTypeId) {
        this.instrumentTypeId = instrumentTypeId;
        instrumentType = InstrumentType.getInstrumentTypeById(instrumentTypeId);
    }

    public InstrumentType getInstrumentType(){
        return instrumentType;
    }
    public void setInstrumentType(InstrumentType instrumentType) {
        this.instrumentType = instrumentType;
        this.instrumentTypeId = instrumentType.getValue();
    }

    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public LocalDateTime getTreelastchanged() {
        return this.treelastchanged;
    }
    public void setTreelastchanged(LocalDateTime treelastchanged) {
        this.treelastchanged = treelastchanged;
    }

    public String getBusinesskey() {
        return this.businesskey;
    }
    public void setBusinesskey(String businesskey) {
        this.businesskey = businesskey;
    }

    public Map<AdditionalProperties, String> getAdditionalProperties() {
        return additionalProperties;
    }
    public void setAdditionalProperties(Map<AdditionalProperties, String> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public Map<AdditionalMaps, Map<String, String>> getAdditionalMaps() {
        return additionalMaps;
    }
    public void setAdditionalMaps(Map<AdditionalMaps, Map<String, String>> additionalMaps) {
        this.additionalMaps = additionalMaps;
    }

    public Integer getVersion() {
        return version;
    }
    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getParentBusinesskey() {
        return parentBusinesskey;
    }

    public void setParentBusinesskey(String parentBusinesskey) {
        this.parentBusinesskey = parentBusinesskey;
    }

    public String getTenantBusinesskey() {
        return tenantBusinesskey;
    }
    public void setTenantBusinesskey(String tenantBusinesskey) {
        this.tenantBusinesskey = tenantBusinesskey;
    }
}
