package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.entities.InstrumentPropertiesEntity;

import java.util.List;
import java.util.Optional;

public interface BaseAccountableInstrumentHandler {
    List<String> getAncestorIds();
    List<InstrumentEntity> getInstrumentChilds(EdgeType edgeType, int pathlength);
    Optional<String> getTenant();
    InstrumentEntity getInstrument();
    InstrumentEntity getInstrument(String errMsg);
    List<InstrumentPropertiesEntity> getInstrumentProperties();
}
