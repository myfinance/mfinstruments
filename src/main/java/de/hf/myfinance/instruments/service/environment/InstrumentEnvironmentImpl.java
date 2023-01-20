package de.hf.myfinance.instruments.service.environment;

import de.hf.framework.audit.AuditService;
import de.hf.myfinance.instruments.persistence.DataReader;
import de.hf.myfinance.instruments.events.out.InstrumentApprovedEventHandler;
import de.hf.myfinance.instruments.service.InstrumentFactory;
import org.springframework.stereotype.Component;

/**
 * to bundle all the necessary environmentobjects of an InstrumentHandler. Otherwise every single object has to be declared in the constructor of the handler which makes a very long constructor
 */
@Component
public record InstrumentEnvironmentImpl(DataReader dataReader,
                                        AuditService auditService,
                                        InstrumentFactory instrumentFactory,
                                        InstrumentApprovedEventHandler eventHandler) implements InstrumentEnvironmentWithFactory {

    @Override
    public AuditService getAuditService() {
        return auditService;
    }

    @Override
    public InstrumentFactory getInstrumentFactory() {
        return instrumentFactory;
    }

    @Override
    public InstrumentApprovedEventHandler getEventHandler() {
        return eventHandler;
    }

    @Override
    public DataReader getDataReader() {
        return dataReader;
    }
}
