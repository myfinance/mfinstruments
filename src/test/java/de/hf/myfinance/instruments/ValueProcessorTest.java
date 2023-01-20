package de.hf.myfinance.instruments;

import de.hf.myfinance.event.Event;
import de.hf.myfinance.restmodel.ValueCurve;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDate;
import java.util.TreeMap;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValueProcessorTest extends EventProcessorTestBase{

    @Autowired
    @Qualifier("valueProcessor")
    protected Consumer<Event<String, ValueCurve>> valueProcessor;

    @Test
    void setInactive() {
        var businesskey = "thekey";
        var valueCurve = new ValueCurve();
        TreeMap<LocalDate, Double> values = new TreeMap<>();
        values.put(LocalDate.of(2022,1,1), 0.0);
        values.put(LocalDate.of(2022,1,2), 1000.0);
        values.put(LocalDate.of(2022,1,3), 0.0);
        valueCurve.setValueCurve(values);
        valueCurve.setInstrumentBusinesskey(businesskey);
        Event creatEvent = new Event(Event.Type.CREATE, businesskey, valueCurve);
        valueProcessor.accept(creatEvent);

        var inActivationInfo = inActivationInfoRepository.findAll().collectList().block();

        assertEquals(1, inActivationInfo.size());

        var inActivationInfoEntry = inActivationInfo.get(0);
        assertEquals(businesskey, inActivationInfoEntry.getBusinesskey());
        assertEquals(true, inActivationInfoEntry.isInactivateable());
    }

    @Test
    void setActive() {
        var businesskey = "thekey";
        var valueCurve = new ValueCurve();
        TreeMap<LocalDate, Double> values = new TreeMap<>();
        values.put(LocalDate.of(2022,1,1), 0.0);
        values.put(LocalDate.of(2022,1,2), 1000.0);
        values.put(LocalDate.of(2022,1,3), 1010.0);
        valueCurve.setValueCurve(values);
        valueCurve.setInstrumentBusinesskey(businesskey);
        Event creatEvent = new Event(Event.Type.CREATE, businesskey, valueCurve);
        valueProcessor.accept(creatEvent);

        var inActivationInfo = inActivationInfoRepository.findAll().collectList().block();

        assertEquals(1, inActivationInfo.size());

        var inActivationInfoEntry = inActivationInfo.get(0);
        assertEquals(businesskey, inActivationInfoEntry.getBusinesskey());
        assertEquals(false, inActivationInfoEntry.isInactivateable());
    }
}
