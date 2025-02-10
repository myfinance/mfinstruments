package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.AdditionalProperties;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;


public class DeprecationObjectHandler  extends AbsAccountHandler {

    public DeprecationObjectHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.DEPRECATIONOBJECT;
    }

    @Override
    protected String initBusinesskey() {
        var iban = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.IBAN);
        if(iban==null || iban.isEmpty()){
            return super.initBusinesskey();
        }
        return iban;
    }
}