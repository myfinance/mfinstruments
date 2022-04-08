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



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


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
    private boolean isactive;
    private LocalDate maturitydate;
    private LocalDate closingdate;
    private LocalDateTime treelastchanged;
    @Indexed(unique = true)
    private String businesskey;
    private Set<InstrumentPropertiesEntity> instrumentProperties = new HashSet<InstrumentPropertiesEntity>(0);

    public InstrumentEntity() {
    }

    public InstrumentEntity(InstrumentType instrumentType, String description, boolean isactive, LocalDateTime treelastchanged) {
        setInstrumentTypeId(instrumentType.getValue());
        this.description = description;
        this.isactive = isactive;
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

    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIsactive() {
        return this.isactive;
    }
    public void setIsactive(boolean isactive) {
        this.isactive = isactive;
    }
    
    public LocalDate getMaturitydate() {
        return this.maturitydate;
    }
    public void setMaturitydate(LocalDate maturitydate) {
        this.maturitydate = maturitydate;
    }
    

    public LocalDate getClosingdate() {
        return this.closingdate;
    }
    public void setClosingdate(LocalDate closingdate) {
        this.closingdate = closingdate;
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

    public Set<InstrumentPropertiesEntity> getInstrumentProperties() {
        return this.instrumentProperties;
    }
    public void setInstrumentProperties(Set<InstrumentPropertiesEntity> instrumentProperties) {
        this.instrumentProperties = instrumentProperties;
    }

    public Integer getVersion() {
        return version;
    }
    public void setVersion(Integer version) {
        this.version = version;
    }
}
