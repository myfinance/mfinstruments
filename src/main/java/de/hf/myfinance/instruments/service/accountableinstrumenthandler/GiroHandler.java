package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithGraph;
import de.hf.myfinance.restmodel.InstrumentType;

public class GiroHandler extends AbsCashInstrumentHandler {

    public GiroHandler(InstrumentEnvironmentWithGraph instrumentEnvironment, String description, String tenantId, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, tenantId, businesskey, isNewInstrument);
    }

    @Override
    protected InstrumentEntity createDomainObject() {
        var theObj = new InstrumentEntity(InstrumentType.GIRO, description, true, ts);
        theObj.setBusinesskey(businesskey);
        return theObj;
    }

    @Override
    protected InstrumentType getParentType() {
        return InstrumentType.ACCOUNTPORTFOLIO;
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.GIRO;
    }

    @Override
    public void setDescription(String description) {
        super.setDescription(description);
        super.setBusinesskey();
    }
}