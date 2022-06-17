package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithGraph;
import de.hf.myfinance.restmodel.InstrumentType;

public class BudgetHandler extends AbsCashInstrumentHandler {

    public BudgetHandler(InstrumentEnvironmentWithGraph instrumentEnvironment, String description, String budgetGroupId, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, budgetGroupId, businesskey, isNewInstrument);
    }

    @Override
    protected InstrumentEntity createDomainObject() {
        var theObj = new InstrumentEntity(InstrumentType.BUDGET, description, true, ts);
        theObj.setBusinesskey(businesskey);
        return theObj;
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