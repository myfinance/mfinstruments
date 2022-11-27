package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;

public class BudgetHandler extends AbsCashInstrumentHandler {

    public BudgetHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
    }

    @Override
    protected Instrument createDomainObject() {
        return new Instrument(businesskey, requestedInstrument.getDescription(), InstrumentType.BUDGET, true, ts);
    }

    @Override
    protected InstrumentType getParentType() {
        return InstrumentType.BUDGETGROUP;
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.BUDGET;
    }
}