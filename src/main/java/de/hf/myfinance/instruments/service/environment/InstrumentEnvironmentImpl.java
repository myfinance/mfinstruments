package de.hf.myfinance.instruments.service.environment;

import de.hf.framework.audit.AuditService;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.events.out.EventHandler;
import de.hf.myfinance.instruments.service.InstrumentFactory;
import org.springframework.stereotype.Component;

/**
 * to bundle all the necessary environmentobjects of an InstrumentHandler. Otherwise every single object has to be declared in the constructor of the handler which makes a very long constructor
 */
@Component
public class InstrumentEnvironmentImpl implements InstrumentEnvironmentWithGraphAndFactory {
    private final InstrumentRepository instrumentRepository;
    private final InstrumentGraphRepository instrumentGraphRepository;
    private final AuditService auditService;
    private final InstrumentFactory instrumentFactory;
    private final EventHandler eventHandler;

    public InstrumentEnvironmentImpl(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, InstrumentFactory instrumentFactory, EventHandler eventHandler) {
        this.instrumentRepository = instrumentRepository;
        this.instrumentGraphRepository = instrumentGraphRepository;
        this.auditService = auditService;
        this.instrumentFactory = instrumentFactory;
        this.eventHandler = eventHandler;
    }

    @Override
    public InstrumentRepository getInstrumentRepository() {
        return instrumentRepository;
    }

    @Override
    public InstrumentGraphRepository getInstrumentGraphRepository() {
        return instrumentGraphRepository;
    }

    @Override
    public AuditService getAuditService() {
        return auditService;
    }

    @Override
    public InstrumentFactory getInstrumentFactory() {
        return instrumentFactory;
    }

    @Override
    public EventHandler getEventHandler() {
        return eventHandler;
    }
}
