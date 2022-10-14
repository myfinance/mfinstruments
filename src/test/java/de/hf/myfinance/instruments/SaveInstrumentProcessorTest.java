package de.hf.myfinance.instruments;


import de.hf.myfinance.event.Event;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SaveInstrumentProcessorTest extends EventProcessorTestBase {

    @Autowired
    @Qualifier("saveInstrumentProcessor")
    protected Consumer<Event<Integer, Instrument>> saveInstrumentProcessor;

    @Test
    void createTenant() {
        var businessKey = "aTest@6";
        var desc = "aTest";
        var newInstrument = new Instrument(desc, InstrumentType.TENANT);
        newInstrument.setBusinesskey(businessKey);
        Event creatEvent = new Event(Event.Type.CREATE, businessKey, newInstrument);
        saveInstrumentProcessor.accept(creatEvent);

        var instruments = instrumentRepository.findAll().collectList().block();
        assertEquals(1, instruments.size());

        var savedInstrument = instruments.get(0);
        assertEquals(businessKey, savedInstrument.getBusinesskey());
        assertEquals(desc, savedInstrument.getDescription());
        assertTrue(savedInstrument.isIsactive());
        assertEquals(InstrumentType.TENANT, savedInstrument.getInstrumentType());
    }

    @Test
    void createBudget() {
        var businessKey = "incomeBgt_bgtGrp_bgtPf_aTest@5";
        var desc = "aTest";
        var newInstrument = new Instrument(desc, InstrumentType.BUDGET);
        newInstrument.setBusinesskey(businessKey);
        Event createEvent = new Event(Event.Type.CREATE, businessKey, newInstrument);
        saveInstrumentProcessor.accept(createEvent);

        var instruments = instrumentRepository.findAll().collectList().block();
        assertEquals(1, instruments.size());

        var savedInstrument = instruments.get(0);
        assertEquals(businessKey, savedInstrument.getBusinesskey());
        assertEquals(desc, savedInstrument.getDescription());
        assertTrue(savedInstrument.isIsactive());
        assertEquals(InstrumentType.BUDGET, savedInstrument.getInstrumentType());
    }
}
