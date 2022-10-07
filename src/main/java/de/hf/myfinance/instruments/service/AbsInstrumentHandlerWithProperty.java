package de.hf.myfinance.instruments.service;

import java.util.*;

import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.restmodel.AdditionalMaps;
import de.hf.myfinance.restmodel.AdditionalProperties;
import de.hf.myfinance.restmodel.Instrument;

public abstract class AbsInstrumentHandlerWithProperty extends AbsInstrumentHandler implements InstrumentHandler {

    protected EnumMap<AdditionalMaps, Map<String, String>> additionalMaps = new EnumMap<>(AdditionalMaps.class);
    protected EnumMap<AdditionalProperties, String> additionalProperties = new EnumMap<>(AdditionalProperties.class);

    protected AbsInstrumentHandlerWithProperty(InstrumentEnvironment instrumentEnvironment, String description, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, businesskey, isNewInstrument);
    }

    @Override
    protected Instrument setAdditionalValues(Instrument instrument) {
        instrument.setAdditionalProperties(additionalProperties);
        instrument.setAdditionalMaps(additionalMaps);
        return instrument;
    }

}