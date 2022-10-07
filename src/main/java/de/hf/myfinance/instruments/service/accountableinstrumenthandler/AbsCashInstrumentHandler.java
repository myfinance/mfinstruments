package de.hf.myfinance.instruments.service.accountableinstrumenthandler;


import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;

public abstract class AbsCashInstrumentHandler extends AbsAccountableInstrumentHandler {

    protected AbsCashInstrumentHandler(InstrumentEnvironment instrumentEnvironment, String description, String parentBusinesskey, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, parentBusinesskey, businesskey, isNewInstrument);
    }

}