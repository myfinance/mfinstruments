package de.hf.myfinance.instruments.persistence.entities;

import java.time.LocalDate;


public class InstrumentPropertiesEntity  implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String propertyname;
    private String value;
    private String valuetype;
    private LocalDate validfrom;

    public InstrumentPropertiesEntity() {
    }


    public InstrumentPropertiesEntity(String propertyname, String value, String valuetype) {
        this.propertyname = propertyname;
        this.value = value;
        this.valuetype = valuetype;
    }
    public InstrumentPropertiesEntity(String propertyname, String value, String valuetype, LocalDate validfrom) {
        this.propertyname = propertyname;
        this.value = value;
        this.valuetype = valuetype;
        this.validfrom = validfrom;
    }



    public String getPropertyname() {
        return this.propertyname;
    }

    public void setPropertyname(String propertyname) {
        this.propertyname = propertyname;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValuetype() {
        return this.valuetype;
    }

    public void setValuetype(String valuetype) {
        this.valuetype = valuetype;
    }

    public LocalDate getValidfrom() {
        return this.validfrom;
    }

    public void setValidfrom(LocalDate validfrom) {
        this.validfrom = validfrom;
    }

}
