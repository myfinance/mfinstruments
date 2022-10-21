package de.hf.myfinance.instruments.service;

import java.time.LocalDateTime;

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
    // set to true if the instrument is created by code and not API request e.G. a Tenant creates a AccountPortfolio automaticly and a Parent validation makes no sense(parent is not available at this point but will be
    void setIsSimpleValidation(boolean isSimpleValidation);
}