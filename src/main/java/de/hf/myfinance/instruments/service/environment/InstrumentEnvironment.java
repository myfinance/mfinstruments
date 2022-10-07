package de.hf.myfinance.instruments.service.environment;

import de.hf.framework.audit.AuditService;
import de.hf.myfinance.instruments.persistence.DataReader;
import de.hf.myfinance.instruments.events.out.EventHandler;

public interface InstrumentEnvironment {
    DataReader getDataReader();
    AuditService getAuditService();
    EventHandler getEventHandler();
}
