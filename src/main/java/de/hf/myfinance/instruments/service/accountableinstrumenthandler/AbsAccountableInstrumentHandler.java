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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

/**
 * This abstract class is the base for all Instruments a Tenant can be directly connected with and the Tenant it self.
 * Securities like Equities  and Bonds are only connected via Trades and so use a different base class
 */
public abstract class AbsAccountableInstrumentHandler extends AbsInstrumentHandlerWithProperty implements AccountableInstrumentHandler{
    private String parentId;
    protected final InstrumentGraphHandler instrumentGraphHandler;
    boolean addToAccountPf;
    boolean isRootElement = false;

    protected AbsAccountableInstrumentHandler(InstrumentEnvironmentWithGraph instrumentEnvironment, String description, String parentId, String businesskey, boolean isNewInstrument) {
        this(instrumentEnvironment, description, parentId, false, businesskey, isNewInstrument);
    }

    protected AbsAccountableInstrumentHandler(InstrumentEnvironmentWithGraph instrumentEnvironment, String description, String parentId, boolean addToAccountPf, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, businesskey, isNewInstrument);
        this.instrumentGraphHandler = new InstrumentGraphHandlerImpl(instrumentEnvironment);
        if(isNewInstrument) {
            this.parentId = parentId;
            this.addToAccountPf = addToAccountPf;
        }
    }

    protected AbsAccountableInstrumentHandler(InstrumentEnvironmentWithGraph instrumentEnvironment, String businesskey) {
        super(instrumentEnvironment, "", businesskey, false);
        this.instrumentGraphHandler = new InstrumentGraphHandlerImpl(instrumentEnvironment);
    }

    @Override
    public Mono<InstrumentEntity> loadInstrument() {
        var instrumentMono = super.loadInstrument();
        if(isNewInstrument && !isRootElement) {
            var parentMono = loadParent();
            return Mono.zip(instrumentMono, parentMono, this::validateParent);
        }

        return instrumentMono;
    }

    protected Mono<InstrumentEntity> loadParent() {
        if(isRootElement) {
            // the tenant or Root element has no parent
            return Mono.empty();
        }
        if(addToAccountPf) {
            return getAllInstrumentChildsWithType(parentId, InstrumentType.ACCOUNTPORTFOLIO)
                    .switchIfEmpty(Mono.error(new MFException(MFMsgKey.UNKNOWN_INSTRUMENT_EXCEPTION,  "Account not saved: account portfolio for the tenant:"+parentId+" does not exists")))
                    .next();
        }
        return instrumentRepository.findById(parentId)
                .switchIfEmpty(Mono.error(new MFException(MFMsgKey.UNKNOWN_PARENT_EXCEPTION, domainObjectName+" not saved: unknown parent:"+parentId)));
    }

    protected Flux<InstrumentEntity> getAllInstrumentChildsWithType(String parentId, InstrumentType instrumentType) {
        return getInstrumentChilds(instrumentId, EdgeType.TENANTGRAPH, 0)
                .filter(e -> e.getInstrumentType().equals(instrumentType));
    }

    protected Mono<InstrumentEntity> saveNewInstrument(InstrumentEntity instrumentEntity) {
        return super.saveNewInstrument(instrumentEntity).flatMapMany(this::saveGraph).then(this.loadInstrument());
    }



    protected Flux<InstrumentGraphEntry> saveGraph(InstrumentEntity instrumentEntity) {
        this.instrumentId = instrumentEntity.getInstrumentid();
        if(isRootElement) parentId = instrumentId;
        return instrumentGraphHandler.addInstrumentToGraph(instrumentId, parentId);
    }


    protected InstrumentEntity validateParent(InstrumentEntity instrument, InstrumentEntity parent) {
        if(isRootElement) {
            // the tenant or Root element has no parent
            return instrument;
        }
        if(parent.getInstrumentType() != getParentType()){
            throw new MFException(MFMsgKey.WRONG_INSTRUMENTTYPE_EXCEPTION,  domainObjectName+" not saved: Instrument with Id "+parentId + " has the wrong type");
        }
        if(addToAccountPf) this.parentId = parent.getInstrumentid();
        return instrument;
    }

    protected InstrumentType getParentType() {
        return InstrumentType.TENANT;
    }


    public Mono<String> getTenant() {
        return instrumentGraphHandler.getRootInstrument(instrumentId, EdgeType.TENANTGRAPH);
    }

    public Flux<InstrumentEntity> getInstrumentChilds(EdgeType edgeType, int pathlength){
        return getInstrumentChilds(instrumentId, edgeType, pathlength);
    }

    protected Flux<InstrumentEntity> getInstrumentChilds(String instrumentId, EdgeType edgeType, int pathlength){
        return instrumentGraphHandler.getInstrumentChildIds(instrumentId, edgeType, pathlength)
                .reduce(new ArrayList<String>(), (e1,e2)-> {
                    e1.add(e2);
                    return e1;
                }).flatMapMany(e->{
                    Flux<InstrumentEntity> instruments = instrumentRepository.findAllById(e);
                    return instruments;
                });
    }

    public Flux<String> getInstrumentChildIds(EdgeType edgeType, int pathlength){
        return instrumentGraphHandler.getInstrumentChildIds(instrumentId, edgeType, pathlength);
    }

    public Flux<String> getAncestorIds() {
        return instrumentGraphHandler.getAncestors(instrumentId, EdgeType.TENANTGRAPH).map(e->e.getAncestor());
    }
}