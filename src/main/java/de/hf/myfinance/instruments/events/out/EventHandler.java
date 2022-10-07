package de.hf.myfinance.instruments.events.out;

import de.hf.myfinance.event.Event;
import de.hf.myfinance.instruments.service.InstrumentMapper;
import de.hf.myfinance.restmodel.Instrument;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static de.hf.myfinance.event.Event.Type.CREATE;

@Component
public class EventHandler {

    private final StreamBridge streamBridge;
    private final InstrumentMapper instrumentMapper;

    public EventHandler(StreamBridge streamBridge, InstrumentMapper instrumentMapper){
        this.streamBridge = streamBridge;
        this.instrumentMapper = instrumentMapper;
    }

    public void sendInstrumentUpdatedEvent(Instrument instrument){
        sendMessage("instrumentapproved-out-0",
                new Event(CREATE, instrument.getBusinesskey().hashCode(), instrument));
    }

    private void sendMessage(String bindingName, Event event) {
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getKey())
                .build();
        streamBridge.send(bindingName, message);
    }
}
