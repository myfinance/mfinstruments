package de.hf.myfinance.instruments.service.environment;

import de.hf.framework.audit.AuditService;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.events.out.EventHandler;

public interface InstrumentEnvironment {
    InstrumentRepository getInstrumentRepository();
    AuditService getAuditService();
    EventHandler getEventHandler();
}
