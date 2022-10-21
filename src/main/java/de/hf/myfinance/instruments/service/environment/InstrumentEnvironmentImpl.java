package de.hf.myfinance.instruments.service.environment;

import de.hf.framework.audit.AuditService;
import de.hf.myfinance.instruments.persistence.DataReader;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.events.out.EventHandler;
import de.hf.myfinance.instruments.service.InstrumentFactory;
import org.springframework.stereotype.Component;

/**
 * to bundle all the necessary environmentobjects of an InstrumentHandler. Otherwise every single object has to be declared in the constructor of the handler which makes a very long constructor
 */
@Component
public class InstrumentEnvironmentImpl implements InstrumentEnvironmentWithFactory {
    private final DataReader dataReader;
    private final AuditService auditService;
    private final InstrumentFactory instrumentFactory;
    private final EventHandler eventHandler;

    public InstrumentEnvironmentImpl(DataReader dataReader, AuditService auditService, InstrumentFactory instrumentFactory, EventHandler eventHandler) {
        this.dataReader = dataReader;
        this.auditService = auditService;
        this.instrumentFactory = instrumentFactory;
        this.eventHandler = eventHandler;
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

    @Override
    public DataReader getDataReader(){
        return dataReader;
    }
}
