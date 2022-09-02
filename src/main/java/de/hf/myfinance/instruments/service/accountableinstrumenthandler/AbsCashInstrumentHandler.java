package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithGraph;


public abstract class AbsCashInstrumentHandler extends AbsAccountableInstrumentHandler {

    protected AbsCashInstrumentHandler(InstrumentEnvironmentWithGraph instrumentEnvironment, String description, String parentBusinesskey, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, parentBusinesskey, businesskey, isNewInstrument);
    }

}