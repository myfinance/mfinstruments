package de.hf.myfinance.instruments.events.out;

import de.hf.myfinance.event.Event;
import de.hf.myfinance.restmodel.Instrument;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static de.hf.myfinance.event.Event.Type.CREATE;

@Component
public class ValidateInstrumentEventHandler {
    private final StreamBridge streamBridge;

    public ValidateInstrumentEventHandler(StreamBridge streamBridge){
        this.streamBridge = streamBridge;
    }

    public void sendValidateInstrumentRequestEvent(Instrument instrument){
        sendMessage("validateInstrumentRequest-out-0",
                new Event<>(CREATE, instrument.getBusinesskey(), instrument));
    }

    private void sendMessage(String bindingName, Event<String, Instrument> event) {
        Message<Event<String, Instrument>> message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getKey())
                .build();
        streamBridge.send(bindingName, message);
    }
}
