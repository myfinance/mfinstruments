package de.hf.myfinance.instruments.service.securityhandler;

import de.hf.myfinance.instruments.service.AbsInstrumentHandler;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;

public class BondHandler extends AbsInstrumentHandler {
    public BondHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.BOND;
    }
    
}
