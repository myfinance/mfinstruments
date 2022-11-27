package de.hf.myfinance.instruments.service.accountableinstrumenthandler;


import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.Instrument;

public abstract class AbsCashInstrumentHandler extends AbsAccountableInstrumentHandler {

    protected AbsCashInstrumentHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
    }

}