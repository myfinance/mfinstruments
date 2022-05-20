package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.entities.InstrumentGraphEntry;
import de.hf.myfinance.instruments.service.AbsInstrumentHandlerWithProperty;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithGraph;
import de.hf.myfinance.instruments.service.instrumentgraphhandler.InstrumentGraphHandler;
import de.hf.myfinance.instruments.service.instrumentgraphhandler.InstrumentGraphHandlerImpl;
import de.hf.myfinance.restmodel.InstrumentType;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This abstract class is the base for all Instruments a Tenant can be directly connected with and the Tenant it self.
 * Securities like Equities  and Bonds are only connected via Trades and so use a different base class
 */
public abstract class AbsAccountableInstrumentHandler extends AbsInstrumentHandlerWithProperty implements AccountableInstrumentHandler{
    private String parentId;
    protected final InstrumentGraphHandler instrumentGraphHandler;

    protected AbsAccountableInstrumentHandler(InstrumentEnvironmentWithGraph instrumentEnvironment, String description, String parentId, String businesskey, boolean isNewInstrument) {
        this(instrumentEnvironment, description, parentId, false, businesskey, isNewInstrument);
    }

    protected AbsAccountableInstrumentHandler(InstrumentEnvironmentWithGraph instrumentEnvironment, String description, String parentId, boolean addToAccountPf, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, businesskey, isNewInstrument);
        this.instrumentGraphHandler = new InstrumentGraphHandlerImpl(instrumentEnvironment);
        setParent(parentId, addToAccountPf);
        validateParent();
    }

    protected AbsAccountableInstrumentHandler(InstrumentEnvironmentWithGraph instrumentEnvironment, String businesskey) {
        super(instrumentEnvironment, "", businesskey, false);
        this.instrumentGraphHandler = new InstrumentGraphHandlerImpl(instrumentEnvironment);
    }

    protected void saveNewInstrument(InstrumentEntity instrumentEntity) {
        super.saveNewInstrument(instrumentEntity);
        updateParent();
        instrumentGraphHandler.addInstrumentToGraph(instrumentId, parentId);
    }


    protected void validateParent() {
        instrumentRepository.findById(parentId)
                .switchIfEmpty(Mono.error(new MFException(MFMsgKey.UNKNOWN_PARENT_EXCEPTION, domainObjectName+" not saved: unknown parent:"+parentId)))
                .subscribe(i-> {
                    if(i.getInstrumentType() != getParentType()){
                        throw new MFException(MFMsgKey.WRONG_INSTRUMENTTYPE_EXCEPTION,  domainObjectName+" not saved: Instrument with Id "+parentId + " has the wrong type");
                    }
                });
    }

    protected void setParent(String parentId, boolean addToAccountPf) {
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


    public Optional<String> getTenant() {
        return instrumentGraphHandler.getRootInstrument(instrumentId, EdgeType.TENANTGRAPH);
    }

    public List<InstrumentEntity> getInstrumentChilds(EdgeType edgeType, int pathlength){
        return instrumentGraphHandler.getInstrumentChilds(instrumentId, edgeType, pathlength);
    }

    public List<String> getInstrumentChildIds(EdgeType edgeType, int pathlength){
        return instrumentGraphHandler.getInstrumentChildIds(instrumentId, edgeType, pathlength);
    }

    public List<String> getAncestorIds() {
        var ids = new ArrayList<String>();
        final List<InstrumentGraphEntry> ancestorGraphEntries = instrumentGraphHandler.getAncestors(instrumentId, EdgeType.TENANTGRAPH);
        if (ancestorGraphEntries != null && !ancestorGraphEntries.isEmpty()) {
            for (final InstrumentGraphEntry entry : ancestorGraphEntries) {
                ids.add(entry.getAncestor());
            }
        }                
        return ids;
    }

    @Override
    protected void validateInstrument(InstrumentEntity instrument, InstrumentType instrumentType, String errMsg) {
        super.validateInstrument(instrument, instrumentType, errMsg);
        /*Optional<String> tenantOfInstrument = instrumentGraphHandler.getRootInstrument(instrument.getInstrumentid(), EdgeType.TENANTGRAPH);
        if(!tenantOfInstrument.isPresent()){
            throw new MFException(MFMsgKey.WRONG_TENENT_EXCEPTION,  errMsg+" instrument has not the same tenant");
        }
        if(initialized) {
            if(!tenantOfInstrument.get().equals(getTenant().get())) {
                throw new MFException(MFMsgKey.WRONG_TENENT_EXCEPTION,  errMsg+" instrument has not the same tenant");
            }
        }*/
    }
}