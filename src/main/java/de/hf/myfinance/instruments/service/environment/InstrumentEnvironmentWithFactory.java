package de.hf.myfinance.instruments.service.environment;

import de.hf.myfinance.instruments.service.InstrumentFactory;

public interface InstrumentEnvironmentWithFactory extends InstrumentEnvironment{
    InstrumentFactory getInstrumentFactory() ;
}
