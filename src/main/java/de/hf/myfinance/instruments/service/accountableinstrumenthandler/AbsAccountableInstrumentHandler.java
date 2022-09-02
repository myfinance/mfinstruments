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
    private String parentBusinesskey;
    protected final InstrumentGraphHandler instrumentGraphHandler;
    boolean isRootElement = false;

    protected AbsAccountableInstrumentHandler(InstrumentEnvironmentWithGraph instrumentEnvironment, String description, String parentBusinesskey, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, businesskey, isNewInstrument);
        this.instrumentGraphHandler = new InstrumentGraphHandlerImpl(instrumentEnvironment);
        if(isNewInstrument) {
            this.parentBusinesskey = parentBusinesskey;
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
        return instrumentRepository.findByBusinesskey(parentBusinesskey)
                .switchIfEmpty(Mono.error(new MFException(MFMsgKey.UNKNOWN_PARENT_EXCEPTION, domainObjectName+" not saved: unknown parent:"+ parentBusinesskey)));
    }

    protected Flux<InstrumentEntity> getAllInstrumentChildsWithType(InstrumentType instrumentType) {
        return getInstrumentChilds(businesskey, EdgeType.TENANTGRAPH, 0)
                .filter(e -> e.getInstrumentType().equals(instrumentType));
    }

    @Override
    protected Mono<InstrumentEntity> saveNewInstrument(InstrumentEntity instrumentEntity) {
        return super.saveNewInstrument(instrumentEntity).flatMap(e-> saveGraph(e)
        // Return again the mono of the tenant
        .flatMap(bpf-> Mono.just(e)));
    }



    protected Mono<InstrumentGraphEntry> saveGraph(InstrumentEntity instrumentEntity) {
        this.businesskey = instrumentEntity.getBusinesskey();
        if(isRootElement) parentBusinesskey = businesskey;
        return instrumentGraphHandler.addInstrumentToGraph(businesskey, parentBusinesskey);
    }


    protected InstrumentEntity validateParent(InstrumentEntity instrument, InstrumentEntity parent) {
        if(isRootElement) {
            // the tenant or Root element has no parent
            return instrument;
        }
        if(parent.getInstrumentType() != getParentType()){
            throw new MFException(MFMsgKey.WRONG_INSTRUMENTTYPE_EXCEPTION,  domainObjectName+" not saved: Instrument with Id "+ parentBusinesskey + " has the wrong type");
        }
        return instrument;
    }

    protected InstrumentType getParentType() {
        return InstrumentType.TENANT;
    }


    public Mono<String> getTenant() {
        return instrumentGraphHandler.getRootInstrument(instrumentId, EdgeType.TENANTGRAPH);
    }

    public Flux<InstrumentEntity> getInstrumentChilds(EdgeType edgeType, int pathlength){
        return getInstrumentChilds(businesskey, edgeType, pathlength);
    }

    protected Flux<InstrumentEntity> getInstrumentChilds(String businesskey, EdgeType edgeType, int pathlength){
        return instrumentGraphHandler.getInstrumentChildIds(businesskey, edgeType, pathlength)
                .reduce(new ArrayList<String>(), (e1,e2)-> {
                    e1.add(e2);
                    return e1;
                }).flatMapMany(instrumentRepository::findAllById);
    }

    public Flux<String> getAncestorIds() {
        return instrumentGraphHandler.getAncestors(businesskey, EdgeType.TENANTGRAPH).map(InstrumentGraphEntry::getAncestor);
    }

    public Flux<InstrumentEntity> listInstrumentChilds(String businesskey) {
        return listInstrumentChilds(getInstrumentById(businesskey, "instrument not found:"+instrumentId));
    }

    public Flux<InstrumentEntity> listInstrumentChilds() {
        return listInstrumentChilds(loadInstrument());
    }

    public Flux<InstrumentEntity> listFirstLevelInstrumentChilds() {
        return listInstrumentChilds(loadInstrument(), 1);
    }

    public Flux<InstrumentEntity> listFirstLevelInstrumentChilds(InstrumentType instrumentType, boolean onlyActive) {
        return filterInstrumentChilds(listInstrumentChilds(instrumentType, onlyActive), true, instrumentType, onlyActive);
    }

    public Flux<InstrumentEntity> listActiveInstrumentChilds() {
        return filterInstrumentChilds(listInstrumentChilds(), false, null, true);
    }

    public Flux<InstrumentEntity> listInstrumentChilds(InstrumentType instrumentType, boolean onlyActive) {
        return filterInstrumentChilds(listInstrumentChilds(), true, instrumentType, onlyActive);
    }

    private Flux<InstrumentEntity> listInstrumentChilds(Mono<InstrumentEntity> instrument){
        return listInstrumentChilds(instrument, 0);
    }

    protected Flux<InstrumentEntity> listInstrumentChilds(Mono<InstrumentEntity> instrument, int pathlength) {
        return instrument.flatMapMany(e->instrumentGraphHandler.getInstrumentChildIds(e.getBusinesskey(), EdgeType.TENANTGRAPH, pathlength))
                .reduce(new ArrayList<String>(), (result, element) -> {
                    result.add(element);
                    return result;
                })
                .flatMapMany(instrumentRepository::findByBusinesskeyIn);
    }

    protected Flux<InstrumentEntity> filterActiveInstrumentChilds(Flux<InstrumentEntity> childs) {
        return filterInstrumentChilds(childs, false, null, true);
    }

    protected Flux<InstrumentEntity> filterInstrumentChilds(Flux<InstrumentEntity> childs, boolean filterInstrumentType, InstrumentType instrumentType, boolean onlyActive) {
        Flux<InstrumentEntity> instruments = childs;
        if(onlyActive) {
            instruments = childs.filter(InstrumentEntity::isIsactive);
        }
        if(filterInstrumentType) {
            instruments = instruments.filter(i->
                    i.getInstrumentType().equals(instrumentType));
        }
        return instruments;
    }
}