package de.hf.myfinance.instruments.service.environment;

import de.hf.myfinance.instruments.service.InstrumentFactory;

public interface InstrumentEnvironmentWithGraphAndFactory extends InstrumentEnvironmentWithGraph{
    InstrumentFactory getInstrumentFactory() ;
}
