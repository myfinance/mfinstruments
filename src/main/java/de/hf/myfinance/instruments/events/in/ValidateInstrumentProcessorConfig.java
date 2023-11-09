package de.hf.myfinance.instruments.events.in;

import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.event.Event;
import de.hf.myfinance.instruments.service.InstrumentService;
import de.hf.myfinance.restmodel.Instrument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.function.Consumer;

@Configuration
public class ValidateInstrumentProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ValidateInstrumentProcessorConfig.class);

    private final InstrumentService instrumentService;

    @Autowired
    public ValidateInstrumentProcessorConfig(InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }

    @Bean
    public Consumer<Event<String, Instrument>> validateInstrumentProcessor() {
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            switch (event.getEventType()) {

                case CREATE:
                    Instrument instrument = event.getData();
                    LOG.info("Create instrument with ID: {}", instrument.getBusinesskey());
                    try{
                        instrumentService.saveInstrument(instrument).block();
                    }catch(MFException e){
                        //no need to throw mfExceptions. These are Validation-Errors and retry the message makes no sense.  
                    }
                    break;

                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE event";
                    LOG.warn(errorMessage);
            }

            LOG.info("Message processing done!");

        };
    }
}