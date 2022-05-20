package de.hf.myfinance.instruments.service.environment;

import de.hf.framework.audit.AuditService;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;

public interface InstrumentEnvironment {
    InstrumentRepository getInstrumentRepository();
    AuditService getAuditService();
}
