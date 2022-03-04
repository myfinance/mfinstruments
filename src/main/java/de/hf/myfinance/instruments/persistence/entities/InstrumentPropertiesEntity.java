package de.hf.myfinance.instruments.persistence.entities;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name="mf_instrumentproperties")
public class InstrumentPropertiesEntity  implements java.io.Serializable {
    private static final long serialVersionUID = 1L;


    private Integer propertyid;
    private String propertyname;
    private int instrumentid;
    private String value;
    private String valuetype;
    private LocalDate validfrom;
    private LocalDate validto;

    public InstrumentPropertiesEntity() {
    }


    public InstrumentPropertiesEntity(String propertyname, int instrumentid, String value, String valuetype) {
        this.propertyname = propertyname;
        this.instrumentid = instrumentid;
        this.value = value;
        this.valuetype = valuetype;
    }
    public InstrumentPropertiesEntity(String propertyname, int instrumentid, String value, String valuetype, LocalDate validfrom, LocalDate validto) {
        this.propertyname = propertyname;
        this.instrumentid = instrumentid;
        this.value = value;
        this.valuetype = valuetype;
        this.validfrom = validfrom;
        this.validto = validto;
    }

    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name="propertyid", unique=true, nullable=false)
    public Integer getPropertyid() {
        return this.propertyid;
    }

    public void setPropertyid(Integer propertyid) {
        this.propertyid = propertyid;
    }

    @Column(name="propertyname", nullable=false, length=64)
    public String getPropertyname() {
        return this.propertyname;
    }

    public void setPropertyname(String propertyname) {
        this.propertyname = propertyname;
    }

    @Column(name="instrumentid", nullable=false)
    public int getInstrumentid() {
        return this.instrumentid;
    }

    public void setInstrumentid(int instrumentid) {
        this.instrumentid = instrumentid;
    }

    @Column(name="value", nullable=false, length=64)
    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Column(name="valuetype", nullable=false, length=32)
    public String getValuetype() {
        return this.valuetype;
    }

    public void setValuetype(String valuetype) {
        this.valuetype = valuetype;
    }

    @Column(name="validfrom", length=13)
    public LocalDate getValidfrom() {
        return this.validfrom;
    }

    public void setValidfrom(LocalDate validfrom) {
        this.validfrom = validfrom;
    }

    @Column(name="validto", length=13)
    public LocalDate getValidto() {
        return this.validto;
    }

    public void setValidto(LocalDate validto) {
        this.validto = validto;
    }
}
