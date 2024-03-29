package de.hf.myfinance.instruments.events.in;

import de.hf.framework.audit.AuditService;
import de.hf.framework.audit.Severity;
import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.event.Event;
import de.hf.myfinance.instruments.events.out.ValidateInstrumentEventHandler;
import de.hf.myfinance.instruments.persistence.DataReader;
import de.hf.myfinance.instruments.persistence.entities.InActivationInfoEntity;
import de.hf.myfinance.instruments.persistence.repositories.InActivationInfoRepository;
import de.hf.myfinance.restmodel.InstrumentTypeGroup;
import de.hf.myfinance.restmodel.ValueCurve;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.TreeMap;
import java.util.function.Consumer;

@Configuration
public class ValueProcessorConfig {

    private final InActivationInfoRepository inActivationInfoRepository;
    private final AuditService auditService;
    private final DataReader dataReader;
    protected static final String AUDIT_MSG_TYPE = "valueProcessor_Event";
    private final ValidateInstrumentEventHandler eventHandler;

    @Autowired
    public ValueProcessorConfig(AuditService auditService, InActivationInfoRepository inActivationInfoRepository,
            DataReader dataReader, ValidateInstrumentEventHandler eventHandler) {
        this.inActivationInfoRepository = inActivationInfoRepository;
        this.auditService = auditService;
        this.dataReader = dataReader;
        this.eventHandler = eventHandler;
    }

    @Bean
    public Consumer<Event<String, ValueCurve>> valueProcessor() {
        return event -> {

            try {
                auditService.saveMessage("Process Value in InstrumentService. message created at " + event.getEventCreatedAt(), Severity.DEBUG,
                        AUDIT_MSG_TYPE);

                if (event.getEventType() == Event.Type.CREATE) {
                    auditService.saveMessage("process valueCurve for inactivatablecheck of instrument with businesskey=" + event.getKey(),
                            Severity.DEBUG, AUDIT_MSG_TYPE);

                    inActivationInfoRepository.findByBusinesskey(event.getData().getInstrumentBusinesskey())
                            .switchIfEmpty(handleNotExistingInstrument(event.getData().getInstrumentBusinesskey()))
                            .flatMap(e -> inActivationInfoRepository
                                    .save(processValueInformation(e, event.getData().getValueCurve())))
                            // check if instrument is now inactivateable but still inactive -> set
                            // instrument active again and send request for validation
                            .flatMap(e -> {
                                if (!e.isInactivateable()) {
                                    return dataReader.findByBusinesskey(event.getData().getInstrumentBusinesskey())
                                            .flatMap(i -> {
                                                if (!i.isActive() &&
                                                        (i.getInstrumentType().getTypeGroup()
                                                                .equals(InstrumentTypeGroup.CASHACCOUNT)
                                                                || i.getInstrumentType().getTypeGroup()
                                                                        .equals(InstrumentTypeGroup.DEPOT)
                                                                || i.getInstrumentType().getTypeGroup()
                                                                        .equals(InstrumentTypeGroup.DEPRECATIONOBJECT)
                                                                || i.getInstrumentType().getTypeGroup()
                                                                        .equals(InstrumentTypeGroup.PORTFOLIO)
                                                                || i.getInstrumentType().getTypeGroup()
                                                                        .equals(InstrumentTypeGroup.LOAN))) {
                                                    i.setActive(true);
                                                    eventHandler.sendValidateInstrumentRequestEvent(i);
                                                }
                                                return Mono.just("");
                                            });
                                }
                                return Mono.just("");
                            })
                            .block();
                } else {
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a Create event";
                    auditService.saveMessage(errorMessage, Severity.WARN, AUDIT_MSG_TYPE);
                }
                auditService.saveMessage("Process Value in InstrumentService processing done!", Severity.DEBUG, AUDIT_MSG_TYPE);
            } catch (MFException e) {
                // no need to throw mfExceptions. These are Validation-Errors and retry the
                // message makes no sense.
            } catch (Exception e) {
                auditService.saveMessage("unexpected error", Severity.FATAL, AUDIT_MSG_TYPE);
                throw e;
            }
        };
    }

    private Mono<InActivationInfoEntity> handleNotExistingInstrument(String businesskey) {
        var inactivationInfo = new InActivationInfoEntity();
        inactivationInfo.setBusinesskey(businesskey);
        return Mono.just(inactivationInfo);
    }

    private InActivationInfoEntity processValueInformation(InActivationInfoEntity inActivationInfoEntity,
            TreeMap<LocalDate, Double> valueCurve) {
        inActivationInfoEntity.setInactivateable(valueCurve.lastEntry().getValue() == 0.0);
        return inActivationInfoEntity;
    }
}
