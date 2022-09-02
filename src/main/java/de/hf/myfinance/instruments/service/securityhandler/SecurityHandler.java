package de.hf.myfinance.instruments.service.securityhandler;

import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.service.AbsInstrumentHandlerWithProperty;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;

abstract class SecurityHandler extends AbsInstrumentHandlerWithProperty {
    protected SecurityHandler(InstrumentEnvironment instrumentEnvironment, String description, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, businesskey, isNewInstrument);
    }

    @Override
    protected void setBusinesskey() {
        if(isNewInstrument) {
            if(businesskey==null) {
                throw new MFException(MFMsgKey.NO_VALID_INSTRUMENT, "businesskey is necessary for new securities");
            }
            this.businesskey = this.businesskey.replace(" ", "");
            if(this.businesskey.length()> MAX_BUSINESSKEY_SIZE) this.businesskey = this.businesskey.substring(0, MAX_BUSINESSKEY_SIZE);
        }
    }
}
