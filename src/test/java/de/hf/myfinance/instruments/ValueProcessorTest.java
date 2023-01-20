package de.hf.myfinance.instruments;

import de.hf.myfinance.event.Event;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.restmodel.InstrumentType;
import de.hf.myfinance.restmodel.ValueCurve;
import de.hf.testhelper.JsonHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;


class ValueProcessorTest extends EventProcessorTestBase{

    @Autowired
    @Qualifier("valueProcessor")
    protected Consumer<Event<String, ValueCurve>> valueProcessor;

    @Test
    void setInactivatable() {
        var businesskey = "thekey";
        var valueCurve = new ValueCurve();
        TreeMap<LocalDate, Double> values = new TreeMap<>();
        values.put(LocalDate.of(2022,1,1), 0.0);
        values.put(LocalDate.of(2022,1,2), 1000.0);
        values.put(LocalDate.of(2022,1,3), 0.0);
        valueCurve.setValueCurve(values);
        valueCurve.setInstrumentBusinesskey(businesskey);
        Event<String, ValueCurve> creatEvent = new Event<>(Event.Type.CREATE, businesskey, valueCurve);
        valueProcessor.accept(creatEvent);

        var inActivationInfo = inActivationInfoRepository.findAll().collectList().block();
        assertNotNull(inActivationInfo);
        assertEquals(1, inActivationInfo.size());

        var inActivationInfoEntry = inActivationInfo.get(0);
        assertEquals(businesskey, inActivationInfoEntry.getBusinesskey());
        assertTrue(inActivationInfoEntry.isInactivateable());
    }

    @Test
    void setUnInactivatable() {
        var businesskey = "thekey";
        var valueCurve = new ValueCurve();
        TreeMap<LocalDate, Double> values = new TreeMap<>();
        values.put(LocalDate.of(2022,1,1), 0.0);
        values.put(LocalDate.of(2022,1,2), 1000.0);
        values.put(LocalDate.of(2022,1,3), 1010.0);
        valueCurve.setValueCurve(values);
        valueCurve.setInstrumentBusinesskey(businesskey);
        Event<String, ValueCurve> creatEvent = new Event<>(Event.Type.CREATE, businesskey, valueCurve);
        valueProcessor.accept(creatEvent);

        var inActivationInfo = inActivationInfoRepository.findAll().collectList().block();

        assertNotNull(inActivationInfo);
        assertEquals(1, inActivationInfo.size());

        var inActivationInfoEntry = inActivationInfo.get(0);
        assertEquals(businesskey, inActivationInfoEntry.getBusinesskey());
        assertFalse( inActivationInfoEntry.isInactivateable());
    }

    @Test
    void setUnInactivatableWithInactiveInstrument() {
        var businesskey = "thekey";

        var instrument = new InstrumentEntity(InstrumentType.GIRO, "test instrument", false, LocalDateTime.now());
        instrument.setBusinesskey(businesskey);
        instrumentRepository.save(instrument).block();

        var valueCurve = new ValueCurve();
        TreeMap<LocalDate, Double> values = new TreeMap<>();
        values.put(LocalDate.of(2022,1,1), 0.0);
        values.put(LocalDate.of(2022,1,2), 1000.0);
        values.put(LocalDate.of(2022,1,3), 0.0);
        values.put(LocalDate.of(2022,1,4), 1010.0);
        valueCurve.setValueCurve(values);
        valueCurve.setInstrumentBusinesskey(businesskey);
        Event<String, ValueCurve> creatEvent = new Event<>(Event.Type.CREATE, businesskey, valueCurve);
        valueProcessor.accept(creatEvent);

        var inActivationInfo = inActivationInfoRepository.findAll().collectList().block();

        assertNotNull(inActivationInfo);
        assertEquals(1, inActivationInfo.size());

        var inActivationInfoEntry = inActivationInfo.get(0);
        assertEquals(businesskey, inActivationInfoEntry.getBusinesskey());
        assertFalse(inActivationInfoEntry.isInactivateable());

        final List<String> messages = getMessages("validateInstrumentRequest-out-0");

        assertEquals(1, messages.size());
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(businesskey, data.get("businesskey"));
        assertTrue((boolean)data.get("active"));
    }

    @Test
    void setUnInactivatableWithInactiveEquity() {
        var businesskey = "theEquity";

        var instrument = new InstrumentEntity(InstrumentType.EQUITY, "test Equity", false, LocalDateTime.now());
        instrument.setBusinesskey(businesskey);
        instrumentRepository.save(instrument).block();

        var valueCurve = new ValueCurve();
        TreeMap<LocalDate, Double> values = new TreeMap<>();
        values.put(LocalDate.of(2022,1,1), 0.0);
        values.put(LocalDate.of(2022,1,2), 1000.0);
        values.put(LocalDate.of(2022,1,3), 0.0);
        values.put(LocalDate.of(2022,1,4), 1010.0);
        valueCurve.setValueCurve(values);
        valueCurve.setInstrumentBusinesskey(businesskey);
        Event<String, ValueCurve> creatEvent = new Event<>(Event.Type.CREATE, businesskey, valueCurve);
        valueProcessor.accept(creatEvent);

        var inActivationInfo = inActivationInfoRepository.findAll().collectList().block();

        assertNotNull(inActivationInfo);
        assertEquals(1, inActivationInfo.size());

        var inActivationInfoEntry = inActivationInfo.get(0);
        assertEquals(businesskey, inActivationInfoEntry.getBusinesskey());
        assertFalse(inActivationInfoEntry.isInactivateable());

        final List<String> messages = getMessages("validateInstrumentRequest-out-0");

        assertEquals(0, messages.size());
    }
}
