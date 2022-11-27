package de.hf.myfinance.instruments.service;

import java.time.LocalDateTime;

import de.hf.myfinance.restmodel.Instrument;
import reactor.core.publisher.Mono;

public interface InstrumentHandler {
    void setTreeLastChanged(LocalDateTime ts);
    Mono<String> save();
    Mono<Instrument> loadInstrument();
    // set to true if the instrument is created by code and not API request e.G. a Tenant creates a AccountPortfolio automaticly and a Parent validation makes no sense(parent is not available at this point but will be
    void setIsSimpleValidation(boolean isSimpleValidation);
}