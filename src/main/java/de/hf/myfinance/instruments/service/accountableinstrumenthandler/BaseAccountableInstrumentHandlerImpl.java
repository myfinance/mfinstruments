package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * this is the most simple instrumenthandler to get type independent informations for the instrument and his tenant without loading it. You only need the instrumentId
 */
public final class BaseAccountableInstrumentHandlerImpl extends AbsAccountableInstrumentHandler implements BaseAccountableInstrumentHandler{


    public BaseAccountableInstrumentHandlerImpl(InstrumentEnvironment instrumentEnvironment, String businesskey) {
        super(instrumentEnvironment, businesskey);
    }

    @Override
    protected Instrument createDomainObject() {
        throw new MFException(MFMsgKey.WRONG_OPERATION_EXCEPTION,  " domainobject can not be spezified for BaseAccountableInstrumentHandler");
    }

    @Override
    protected InstrumentType getInstrumentType() {
        throw new MFException(MFMsgKey.WRONG_OPERATION_EXCEPTION,  " instrumenttype can not be spezified for BaseAccountableInstrumentHandler");
    }

    @Override
    public Mono<String> save() {
        throw new MFException(MFMsgKey.WRONG_OPERATION_EXCEPTION,  "BaseAccountableInstrumentHandler can not be saved");
    }


    public void validateInstrument() {
        throw new MFException(MFMsgKey.WRONG_OPERATION_EXCEPTION,  "BaseAccountableInstrumentHandler can not be loaded and validated");
    }

    @Override
    public void setTreeLastChanged(LocalDateTime ts){
        throw new MFException(MFMsgKey.WRONG_OPERATION_EXCEPTION,  "TimeStamp can not be set for BaseAccountableInstrumentHandler");
    }
}