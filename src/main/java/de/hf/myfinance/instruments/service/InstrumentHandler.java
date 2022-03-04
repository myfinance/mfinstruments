package de.hf.myfinance.instruments.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.entities.InstrumentPropertiesEntity;
import de.hf.myfinance.restmodel.InstrumentPropertyType;

public interface InstrumentHandler {
    InstrumentEntity getInstrument();
    InstrumentEntity getInstrument(String errMsg);
    int getInstrumentId();
    List<InstrumentPropertiesEntity> getInstrumentProperties();
    List<InstrumentPropertiesEntity> getInstrumentProperties(InstrumentPropertyType instrumentPropertyType);
    void validateInstrument();
    void setTreeLastChanged(LocalDateTime ts);
    void save();
    void setActive(boolean isActive);
    void setDescription(String description);
    Optional<InstrumentEntity> getSavedDomainObject() ;
}