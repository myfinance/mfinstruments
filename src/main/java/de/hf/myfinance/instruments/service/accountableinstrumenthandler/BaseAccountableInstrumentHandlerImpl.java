package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithGraph;
import de.hf.myfinance.restmodel.InstrumentType;

import java.time.LocalDateTime;

/**
 * this is the most simple instrumenthandler to get type independent informations for the instrument and his tenant without loading it. You only need the instrumentId
 */
public final class BaseAccountableInstrumentHandlerImpl extends AbsAccountableInstrumentHandler implements BaseAccountableInstrumentHandler{


    public BaseAccountableInstrumentHandlerImpl(InstrumentEnvironmentWithGraph instrumentEnvironment, String businesskey) {
        super(instrumentEnvironment, businesskey);
    }

    @Override
    protected InstrumentEntity createDomainObject() {
        throw new MFException(MFMsgKey.WRONG_OPERATION_EXCEPTION,  " domainobject can not be spezified for BaseAccountableInstrumentHandler");
    }

    @Override
    protected InstrumentType getInstrumentType() {
        throw new MFException(MFMsgKey.WRONG_OPERATION_EXCEPTION,  " instrumenttype can not be spezified for BaseAccountableInstrumentHandler");
    }

    public void save() {
        throw new MFException(MFMsgKey.WRONG_OPERATION_EXCEPTION,  "BaseAccountableInstrumentHandler can not be saved");
    }


    public void validateInstrument() {
        throw new MFException(MFMsgKey.WRONG_OPERATION_EXCEPTION,  "BaseAccountableInstrumentHandler can not be loaded and validated");
    }

    public void setTreeLastChanged(LocalDateTime ts){
        throw new MFException(MFMsgKey.WRONG_OPERATION_EXCEPTION,  "TimeStamp can not be set for BaseAccountableInstrumentHandler");
    }

    public void updateInstrument(boolean isActive) {
        throw new MFException(MFMsgKey.WRONG_OPERATION_EXCEPTION,  "BaseAccountableInstrumentHandler can not be updated");
    }

    public void updateInstrument(String description, boolean isActive) {
        throw new MFException(MFMsgKey.WRONG_OPERATION_EXCEPTION,  "BaseAccountableInstrumentHandler can not be updated");
    }
}