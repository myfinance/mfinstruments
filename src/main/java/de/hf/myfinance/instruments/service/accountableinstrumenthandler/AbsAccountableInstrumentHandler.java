package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.framework.audit.AuditService;
import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.entities.InstrumentGraphEntry;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.service.AbsInstrumentHandlerWithProperty;
import de.hf.myfinance.instruments.service.instrumentgraphhandler.InstrumentGraphHandler;
import de.hf.myfinance.instruments.service.instrumentgraphhandler.InstrumentGraphHandlerImpl;
import de.hf.myfinance.restmodel.InstrumentType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This abstract class is the base for all Instruments a Tenant can be directly connected with and the Tenant it self.
 * Securities like Equities  and Bonds are only connected via Trades and so use a different base class
 */
public abstract class AbsAccountableInstrumentHandler extends AbsInstrumentHandlerWithProperty implements AccountableInstrumentHandler{
    protected final InstrumentGraphHandler instrumentGraphHandler;
    private int parentId;

    protected AbsAccountableInstrumentHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, String description, int parentId, String businesskey) {
        this(instrumentRepository, instrumentGraphRepository, auditService, description, parentId, false, businesskey);
    }

    protected AbsAccountableInstrumentHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, String description, int parentId, boolean addToAccountPf, String businesskey) {
        super(instrumentRepository, auditService, description, businesskey);
        this.instrumentGraphHandler = new InstrumentGraphHandlerImpl(instrumentGraphRepository);
        setParent(parentId, addToAccountPf);
        validateParent();
    }

    protected AbsAccountableInstrumentHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, int instrumentId) {
        super(instrumentRepository, auditService, instrumentId);
        this.instrumentGraphHandler = new InstrumentGraphHandlerImpl(instrumentGraphRepository);
    }

    protected AbsAccountableInstrumentHandler(InstrumentRepository instrumentRepository, InstrumentGraphRepository instrumentGraphRepository, AuditService auditService, InstrumentEntity instrument) {
        super(instrumentRepository, auditService, instrument);
        this.instrumentGraphHandler = new InstrumentGraphHandlerImpl(instrumentGraphRepository);
    }

    protected void saveNewInstrument() {
        super.saveNewInstrument();
        updateParent();
        instrumentGraphHandler.addInstrumentToGraph(instrumentId, parentId);
    }


    protected void validateParent() {
        Optional<InstrumentEntity> parent = instrumentRepository.findById(parentId);
        if(!parent.isPresent()){
            throw new MFException(MFMsgKey.UNKNOWN_PARENT_EXCEPTION, domainObjectName+" not saved: unknown parent:"+parentId);
        }
        if(parent.get().getInstrumentType() != getParentType()){
            throw new MFException(MFMsgKey.WRONG_INSTRUMENTTYPE_EXCEPTION,  domainObjectName+" not saved: Instrument with Id "+parentId + " has the wrong type");
        }
    }

    protected void setParent(int parentId, boolean addToAccountPf) {
        this.parentId = parentId;
        if(addToAccountPf) setParentToAccountPf();
    }

    private void setParentToAccountPf() {
        Optional<InstrumentEntity> accportfolio = instrumentGraphHandler.getAllInstrumentChildsWithType(parentId, InstrumentType.ACCOUNTPORTFOLIO).stream().findFirst();
        if(!accportfolio.isPresent()) {
            throw new MFException(MFMsgKey.UNKNOWN_INSTRUMENT_EXCEPTION,  "Account not saved: account portfolio for the tenant:"+parentId+" does not exists");
        }
        this.parentId = accportfolio.get().getInstrumentid();
    }

    /**
     * used to override the parent during the save function. E.G. Tentant sets the parent to himself 
     */
    protected void updateParent() {
    } 

    protected InstrumentType getParentType() {
        return InstrumentType.TENANT;
    }


    public Optional<InstrumentEntity> getTenant() {
        checkInitStatus();
        return instrumentGraphHandler.getRootInstrument(instrumentId, EdgeType.TENANTGRAPH);
    }

    public List<InstrumentEntity> getInstrumentChilds(EdgeType edgeType, int pathlength){
        checkInitStatus();
        return instrumentGraphHandler.getInstrumentChilds(instrumentId, edgeType, pathlength);
    }

    public List<Integer> getAncestorIds() {
        checkInitStatus();
        var ids = new ArrayList<Integer>();
        final List<InstrumentGraphEntry> ancestorGraphEntries = instrumentGraphHandler.getAncestors(instrumentId, EdgeType.TENANTGRAPH);
        if (ancestorGraphEntries != null && !ancestorGraphEntries.isEmpty()) {
            for (final InstrumentGraphEntry entry : ancestorGraphEntries) {
                ids.add(entry.getId().getAncestor());
            }
        }                
        return ids;
    }

    @Override
    protected void validateInstrument(InstrumentEntity instrument, InstrumentType instrumentType, String errMsg) {
        super.validateInstrument(instrument, instrumentType, errMsg);
        Optional<InstrumentEntity> tenantOfInstrument = instrumentGraphHandler.getRootInstrument(instrument.getInstrumentid(), EdgeType.TENANTGRAPH);
        if(!tenantOfInstrument.isPresent()){
            throw new MFException(MFMsgKey.WRONG_TENENT_EXCEPTION,  errMsg+" instrument has not the same tenant");
        }
        if(initialized) {
            if(!tenantOfInstrument.get().equals(getTenant().get())) {
                throw new MFException(MFMsgKey.WRONG_TENENT_EXCEPTION,  errMsg+" instrument has not the same tenant");
            }
        } else if(tenantOfInstrument.get().equals(parentId)) {
            throw new MFException(MFMsgKey.WRONG_TENENT_EXCEPTION,  errMsg+" instrument has not the same tenant");
        }
    }
}