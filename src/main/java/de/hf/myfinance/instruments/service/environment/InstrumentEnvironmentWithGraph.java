package de.hf.myfinance.instruments.service.environment;

import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;

public interface InstrumentEnvironmentWithGraph extends InstrumentEnvironment{
    InstrumentGraphRepository getInstrumentGraphRepository();
}
