package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import java.util.HashMap;
import java.util.Map;

import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.AdditionalMaps;
import de.hf.myfinance.restmodel.AdditionalProperties;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import reactor.core.publisher.Mono;

public class LifeInsuranceHandler extends AbsAccountHandler {

    public LifeInsuranceHandler(InstrumentEnvironment instrumentEnvironment, Instrument instrument) {
        super(instrumentEnvironment, instrument);
    }

    @Override
    protected InstrumentType getInstrumentType() {
        return InstrumentType.LIFEINSURANCE;
    }

    @Override
    protected Mono<Instrument> setAdditionalValues(Instrument instrument) {

        if(requestedInstrument.getAdditionalMaps().containsKey(AdditionalMaps.SURRENDERVALUES)) {
            var values = requestedInstrument.getAdditionalMaps().get(AdditionalMaps.SURRENDERVALUES);

            var additionalMap = new HashMap<AdditionalMaps, Map<String,String>>();
            additionalMap.put(AdditionalMaps.SURRENDERVALUES, values);
            instrument.setAdditionalMaps(additionalMap);
        }

        if(requestedInstrument.getAdditionalProperties()!=null){

            var properties = new HashMap<AdditionalProperties, String>();

            if(requestedInstrument.getAdditionalProperties().get(AdditionalProperties.VALUEBUDGETID)!=null
            && !requestedInstrument.getAdditionalProperties().get(AdditionalProperties.VALUEBUDGETID).isEmpty()) {
                var value = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.VALUEBUDGETID);
                properties.put(AdditionalProperties.VALUEBUDGETID, value);
            } else {
                return auditService.handleMonoError("for Lifeinsurance it is not allowed to have no Valuebudget", AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT).cast(Instrument.class);
            }

            if(requestedInstrument.getAdditionalProperties().get(AdditionalProperties.MATURITYDATE)!=null
            && !requestedInstrument.getAdditionalProperties().get(AdditionalProperties.MATURITYDATE).isEmpty()) {
                var value = requestedInstrument.getAdditionalProperties().get(AdditionalProperties.MATURITYDATE);
                properties.put(AdditionalProperties.MATURITYDATE, value);
            } else {
                return auditService.handleMonoError("for Lifeinsurance it is not allowed to have no MATURITYDATE", AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT).cast(Instrument.class);
            }

            instrument.setAdditionalProperties(properties);
        } else {
            return auditService.handleMonoError("for Lifeinsurance it is not allowed to have no additional properties", AUDIT_MSG_TYPE, MFMsgKey.NO_VALID_INSTRUMENT).cast(Instrument.class);
        }

        return Mono.just(instrument);
    }

}
