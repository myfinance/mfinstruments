package de.hf.myfinance.instruments.service.securityhandler;

import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.InstrumentType;

public class EquityHandler extends SecurityHandler {
    protected EquityHandler(InstrumentEnvironment instrumentEnvironment, String description, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, businesskey, isNewInstrument);
    }

    @Override
    protected InstrumentEntity createDomainObject() {
        var theObj = new InstrumentEntity(InstrumentType.EQUITY, description, true, ts);
        theObj.setBusinesskey(businesskey);
        return theObj;
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.EQUITY;
    }
}
