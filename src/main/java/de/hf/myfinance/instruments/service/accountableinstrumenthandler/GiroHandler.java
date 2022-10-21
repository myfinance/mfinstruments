package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;

public class GiroHandler extends AbsCashInstrumentHandler {

    public GiroHandler(InstrumentEnvironment instrumentEnvironment, String description, String tenantId, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, tenantId, businesskey, isNewInstrument);
    }

    @Override
    protected Instrument createDomainObject() {
        return new Instrument(businesskey, description, InstrumentType.GIRO, true, ts);
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