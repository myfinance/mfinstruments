package de.hf.myfinance.instruments.service;

import java.util.*;

import de.hf.myfinance.instruments.persistence.entities.InstrumentPropertiesEntity;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;

public abstract class AbsInstrumentHandlerWithProperty extends AbsInstrumentHandler implements InstrumentHandler {

    protected AbsInstrumentHandlerWithProperty(InstrumentEnvironment instrumentEnvironment, String description, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, businesskey, isNewInstrument);
    }

    private List<InstrumentPropertiesEntity> properties;

    /*public List<InstrumentPropertiesEntity> getInstrumentProperties() {
        if(!isPropertyInit) {
            properties = domainObject.getInstrumentProperties().stream().toList();
        }
        return properties;
    }

    public List<InstrumentPropertiesEntity> getInstrumentProperties(InstrumentPropertyType instrumentPropertyType) {
        return getInstrumentProperties().stream().filter(i->i.getPropertyname().equals(instrumentPropertyType.name())).collect(Collectors.toList());
    }

    protected void addProperty(InstrumentPropertyType instrumentPropertyType, String value, LocalDate validFrom) {
        var properties = domainObject.getInstrumentProperties();
        properties.add(new InstrumentPropertiesEntity(instrumentPropertyType.name(), value, instrumentPropertyType.getValueType(), validFrom));
    }

    protected void addProperty(InstrumentPropertyType instrumentPropertyType, ValuePerDate value) {
        var properties = domainObject.getInstrumentProperties();
        properties.add(new InstrumentPropertiesEntity(instrumentPropertyType.name(), String.valueOf(value.getValue()), instrumentPropertyType.getValueType(), value.getDate()));
   }

   protected void addProperty(InstrumentPropertyType instrumentPropertyType, String value) {
        var properties = domainObject.getInstrumentProperties();
        properties.add(new InstrumentPropertiesEntity(instrumentPropertyType.name(), value, instrumentPropertyType.getValueType(), null));
    }

    protected void addProperty(InstrumentPropertyType instrumentPropertyType, int value) {
        addProperty(instrumentPropertyType, String.valueOf(value));
    }

    protected void savePropertyList(InstrumentPropertyType instrumentPropertyType, List<ValuePerDate> values) {
        for(var value : values) {
            addProperty(instrumentPropertyType, value);
        } 
    }

    protected void deleteInstrumentPropertyList() {
        var instrumentProperties = domainObject.getInstrumentProperties();
        for (InstrumentPropertiesEntity instrumentProperty : instrumentProperties) {
            String msg = "instrumentProperty for instrument "+instrumentId+" ,type: '"+instrumentProperty.getPropertyname()+
                    "' ,value:" + instrumentProperty.getValue() +
                    "' ,validfrom:" + instrumentProperty.getValidfrom() + " deleted";
            auditService.saveMessage(msg,
                Severity.INFO, AUDIT_MSG_TYPE);
        }
        domainObject.setInstrumentProperties(new HashSet<>());
    }

    protected void deleteInstrumentPropertyList(InstrumentPropertyType propertyType) {
        var instrumentProperties = domainObject.getInstrumentProperties();
        for (InstrumentPropertiesEntity instrumentProperty : instrumentProperties) {
            if(instrumentProperty.getPropertyname().equals(propertyType.name())) {
                String msg = "instrumentProperty for instrument "+instrumentId+" ,type: '"+instrumentProperty.getPropertyname()+
                        "' ,value:" + instrumentProperty.getValue() +
                        "' ,validfrom:" + instrumentProperty.getValidfrom() + " deleted";
                auditService.saveMessage(msg,
                        Severity.INFO, AUDIT_MSG_TYPE);
                domainObject.getInstrumentProperties().remove(instrumentProperty);
            }
        }    
    }*/

}