package de.hf.myfinance.instruments.service.environment;

import de.hf.framework.audit.AuditService;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
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

    public InstrumentEnvironmentImpl(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, InstrumentFactory instrumentFactory) {
        this.instrumentRepository = instrumentRepository;
        this.instrumentGraphRepository = instrumentGraphRepository;
        this.auditService = auditService;
        this.instrumentFactory = instrumentFactory;
    }

    public InstrumentRepository getInstrumentRepository() {
        return instrumentRepository;
    }

    public InstrumentGraphRepository getInstrumentGraphRepository() {
        return instrumentGraphRepository;
    }

    public AuditService getAuditService() {
        return auditService;
    }

    public InstrumentFactory getInstrumentFactory() {
        return instrumentFactory;
    }
}
