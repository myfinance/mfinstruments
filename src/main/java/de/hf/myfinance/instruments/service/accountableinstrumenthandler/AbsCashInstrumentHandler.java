package de.hf.myfinance.instruments.service.accountableinstrumenthandler;


import de.hf.framework.audit.AuditService;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;


public abstract class AbsCashInstrumentHandler extends AbsAccountableInstrumentHandler {

    public AbsCashInstrumentHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, String description, String tenantId, String businesskey) {
        super(instrumentRepository, instrumentGraphRepository, auditService, description, tenantId, businesskey);

    }

    public AbsCashInstrumentHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, InstrumentEntity cashInstrument) {
        super(instrumentRepository, instrumentGraphRepository, auditService, cashInstrument);
    }

    public AbsCashInstrumentHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, String instrumentId) {
        super(instrumentRepository, instrumentGraphRepository, auditService, instrumentId);
    }

    protected AbsCashInstrumentHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, String description, String tenantId, boolean addToAccountPf, String businesskey) {
        super(instrumentRepository, instrumentGraphRepository, auditService, description, tenantId, addToAccountPf, businesskey);
    }

    @Override
    protected void validateInstrument4Inactivation() {
        /*if( valueService.getValue(instrumentId, LocalDate.MAX)!=0.0 ){
            throw new MFException(MFMsgKey.NO_VALID_INSTRUMENT_FOR_DEACTIVATION, "instrument with id:"+instrumentId + " not deactivated. The current value is not 0");
        } 
        validateRecurrentTransactions4InstrumentInactivation();*/
    }

    
    /*private void validateRecurrentTransactions4InstrumentInactivation() {
        for (RecurrentTransaction r : recurrentTransactionDao.listRecurrentTransactions()) {

            if( r.getInstrumentByInstrumentid1().getInstrumentid() == instrumentId ||
                    r.getInstrumentByInstrumentid2().getInstrumentid() == instrumentId ) {
                throw new MFException(MFMsgKey.NO_VALID_INSTRUMENT_FOR_DEACTIVATION, "instrument with id:"+
                        instrumentId + " not deactivated. There are still recurrent transactions for this instrument");
            }
        }
    }*/
}