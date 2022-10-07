package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;

public class BudgetHandler extends AbsCashInstrumentHandler {

    public BudgetHandler(InstrumentEnvironment instrumentEnvironment, String description, String budgetGroupId, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, budgetGroupId, businesskey, isNewInstrument);
    }

    @Override
    protected Instrument createDomainObject() {
        return new Instrument(businesskey, description, InstrumentType.BUDGET, true, ts);
    }

    @Override
    protected InstrumentType getParentType() {
        return InstrumentType.BUDGETGROUP;
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.BUDGET;
    }

    @Override
    public void setDescription(String description) {
        super.setDescription(description);
        super.setBusinesskey();
    }
}