package de.hf.myfinance.instruments.service.accountableinstrumenthandler;


import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;

public abstract class AbsAccountHandler extends AbsAccountableInstrumentHandler {

    protected AbsAccountHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
    }

    @Override
    protected InstrumentType getParentType() {
        return InstrumentType.ACCOUNTPORTFOLIO;
    }

}