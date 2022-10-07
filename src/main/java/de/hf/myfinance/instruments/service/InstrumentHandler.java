package de.hf.myfinance.instruments.service;

import java.time.LocalDateTime;

import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.restmodel.Instrument;
import reactor.core.publisher.Mono;

public interface InstrumentHandler {
    String getInstrumentId();
    void setTreeLastChanged(LocalDateTime ts);
    Mono<String> save();
    void setActive(boolean isActive);
    void setDescription(String description);
    Mono<Instrument> loadInstrument();
    void setValues(Instrument instrument);
}