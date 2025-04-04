package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import java.util.HashMap;

import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.AdditionalProperties;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import de.hf.myfinance.restmodel.LiquidityType;
import reactor.core.publisher.Mono;

public class LoanHandler extends AbsAccountHandler {

    public LoanHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.LOAN;
    }

    @Override
    protected Mono<Instrument> setLiquidityType(Instrument instrument) {
        instrument.setLiquidityTypeCalculated(true);
        instrument.setLiquidityType(LiquidityType.UNKNOWN);
        return Mono.just(instrument);
    }

    @Override
    protected Mono<Instrument> setAdditionalValues(Instrument instrument) {
        if(requestedInstrument.getAdditionalProperties()!=null){

            var properties = new HashMap<AdditionalProperties, String>();

            if(requestedInstrument.getAdditionalProperties().get(AdditionalProperties.INTERESTRATE)!=null
            && !requestedInstrument.getAdditionalProperties().get(AdditionalProperties.INTERESTRATE).isEmpty()) {
                var value = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.INTERESTRATE);
                properties.put(AdditionalProperties.INTERESTRATE, value);
            } else {
                return auditService.handleMonoError("for Loan it is not allowed to have no INTERESTRATE", AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT).cast(Instrument.class);
            }

            if(requestedInstrument.getAdditionalProperties().get(AdditionalProperties.ANNUITYRATE)!=null
            && !requestedInstrument.getAdditionalProperties().get(AdditionalProperties.ANNUITYRATE).isEmpty()) {
                var value = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.ANNUITYRATE);
                properties.put(AdditionalProperties.ANNUITYRATE, value);
            } else {
                return auditService.handleMonoError("for Loan it is not allowed to have no ANNUITYRATE", AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT).cast(Instrument.class);
            }

            if(requestedInstrument.getAdditionalProperties().get(AdditionalProperties.REFERENCEGIRO)!=null
            && !requestedInstrument.getAdditionalProperties().get(AdditionalProperties.REFERENCEGIRO).isEmpty()) {
                var value = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.REFERENCEGIRO);
                properties.put(AdditionalProperties.REFERENCEGIRO, value);
            } else {
                return auditService.handleMonoError("for Loan it is not allowed to have no REFERENCEGIRO", AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT).cast(Instrument.class);
            }

            if(requestedInstrument.getAdditionalProperties().get(AdditionalProperties.MATURITYDATE)!=null
            && !requestedInstrument.getAdditionalProperties().get(AdditionalProperties.MATURITYDATE).isEmpty()) {
                var value = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.MATURITYDATE);
                properties.put(AdditionalProperties.MATURITYDATE, value);
            } else {
                return auditService.handleMonoError("for Loan it is not allowed to have no MATURITYDATE", AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT).cast(Instrument.class);
            }
            instrument.setAdditionalProperties(properties);
        } else {
            return auditService.handleMonoError("for Loan it is not allowed to have no additional properties", AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT).cast(Instrument.class);
        }

        return Mono.just(instrument);
    }

}