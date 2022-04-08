package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;

import java.util.List;
import java.util.Optional;

public interface AccountableInstrumentHandler {
    Optional<String> getTenant();
    List<InstrumentEntity> getInstrumentChilds(EdgeType edgeType, int pathlength);
    List<String> getAncestorIds();
}