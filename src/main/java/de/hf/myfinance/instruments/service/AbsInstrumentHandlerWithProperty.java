package de.hf.myfinance.instruments.service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import de.hf.framework.audit.AuditService;
import de.hf.framework.audit.Severity;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.persistence.entities.InstrumentPropertiesEntity;
import de.hf.myfinance.restmodel.InstrumentPropertyType;
import de.hf.myfinance.restmodel.ValuePerDate;

public abstract class AbsInstrumentHandlerWithProperty extends AbsInstrumentHandler implements InstrumentHandler {

    protected AbsInstrumentHandlerWithProperty(InstrumentRepository instrumentRepository, AuditService auditService, String description, String businesskey) {
        super(instrumentRepository, auditService, description, businesskey);
    }

    protected AbsInstrumentHandlerWithProperty(InstrumentRepository instrumentRepository, AuditService auditService, String businesskey) {
        super(instrumentRepository, auditService, businesskey);
    }

    private List<InstrumentPropertiesEntity> properties;

    public List<InstrumentPropertiesEntity> getInstrumentProperties() {
        checkInitStatus();
        if(!isPropertyInit) {
            properties = domainObject.getInstrumentProperties().stream().toList();
        }
        return properties;
    }

    public List<InstrumentPropertiesEntity> getInstrumentProperties(InstrumentPropertyType instrumentPropertyType) {
        return getInstrumentProperties().stream().filter(i->i.getPropertyname().equals(instrumentPropertyType.name())).collect(Collectors.toList());
    }

    protected void addProperty(InstrumentPropertyType instrumentPropertyType, String value, LocalDate validFrom) {
        checkInitStatus();
        var properties = domainObject.getInstrumentProperties();
        properties.add(new InstrumentPropertiesEntity(instrumentPropertyType.name(), value, instrumentPropertyType.getValueType(), validFrom));
    }

    protected void addProperty(InstrumentPropertyType instrumentPropertyType, ValuePerDate value) {
        checkInitStatus();
        var properties = domainObject.getInstrumentProperties();
        properties.add(new InstrumentPropertiesEntity(instrumentPropertyType.name(), String.valueOf(value.getValue()), instrumentPropertyType.getValueType(), value.getDate()));
   }

   protected void addProperty(InstrumentPropertyType instrumentPropertyType, String value) {
        checkInitStatus();
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
    }

}