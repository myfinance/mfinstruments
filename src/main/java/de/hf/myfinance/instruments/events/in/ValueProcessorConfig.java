package de.hf.myfinance.instruments.events.in;

import de.hf.framework.audit.AuditService;
import de.hf.framework.audit.Severity;
import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.event.Event;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.persistence.entities.InActivationInfoEntity;
import de.hf.myfinance.instruments.persistence.repositories.InActivationInfoRepository;
import de.hf.myfinance.restmodel.Instrument;
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
    protected static final String AUDIT_MSG_TYPE="valueProcessor_Event";

    @Autowired
    public ValueProcessorConfig(AuditService auditService, InActivationInfoRepository inActivationInfoRepository) {
        this.inActivationInfoRepository = inActivationInfoRepository;
        this.auditService = auditService;
    }

    @Bean
    public Consumer<Event<String, ValueCurve>> valueProcessor() {
        return event -> {
            auditService.saveMessage("Process message created at "+event.getEventCreatedAt(),Severity.INFO, AUDIT_MSG_TYPE);

            switch (event.getEventType()) {

                case CREATE:
                    auditService.saveMessage("process valueCurve of instrument with businesskey=" + event.getKey(), Severity.INFO, AUDIT_MSG_TYPE);

                    inActivationInfoRepository.findByBusinesskey(event.getData().getInstrumentBusinesskey())
                                    .switchIfEmpty(handleNotExistingInstrument(event.getData().getInstrumentBusinesskey()))
                                            .flatMap(e -> {
                                                return inActivationInfoRepository.save(processValueInformation(e, event.getData().getValueCurve()));
                                            }).block();
                    break;

                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a Create event";
                    auditService.saveMessage(errorMessage,Severity.WARN, AUDIT_MSG_TYPE);
            }
            auditService.saveMessage("Message processing done!",Severity.INFO, AUDIT_MSG_TYPE);

        };
    }

    private Mono<InActivationInfoEntity> handleNotExistingInstrument(String businesskey){
        var inactivationInfo = new InActivationInfoEntity();
        inactivationInfo.setBusinesskey(businesskey);
        return Mono.just(inactivationInfo);
    }

    private InActivationInfoEntity processValueInformation(InActivationInfoEntity inActivationInfoEntity, TreeMap<LocalDate, Double> valueCurve) {
        if(valueCurve.lastEntry().getValue()!=0.0) inActivationInfoEntity.setInactivateable(false);
        else inActivationInfoEntity.setInactivateable(true);
        return inActivationInfoEntity;
    }
}
