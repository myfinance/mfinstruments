package de.hf.myfinance.instruments.service.accountableinstrumenthandler;

import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.service.AbsInstrumentHandlerWithProperty;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironment;
import de.hf.myfinance.instruments.service.instrumentgraphhandler.InstrumentGraphHandler;
import de.hf.myfinance.instruments.service.instrumentgraphhandler.InstrumentGraphHandlerImpl;
import de.hf.myfinance.restmodel.Instrument;
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

    protected AbsAccountableInstrumentHandler(InstrumentEnvironment instrumentEnvironment, String description, String parentBusinesskey, String businesskey, boolean isNewInstrument) {
        super(instrumentEnvironment, description, businesskey, isNewInstrument);
        this.instrumentGraphHandler = new InstrumentGraphHandlerImpl(instrumentEnvironment);
        this.parentBusinesskey = parentBusinesskey;
        if(isRootElement) this.parentBusinesskey = businesskey;
    }

    protected AbsAccountableInstrumentHandler(InstrumentEnvironment instrumentEnvironment, String businesskey) {
        super(instrumentEnvironment, "", businesskey, false);
        this.instrumentGraphHandler = new InstrumentGraphHandlerImpl(instrumentEnvironment);
    }

    @Override
    protected Instrument initNewDomainObject() {

        var object = super.initNewDomainObject();
        object.setParentBusinesskey(parentBusinesskey);
        return object;
    }

    @Override
    public Mono<Instrument> loadInstrument() {
        var instrumentMono = super.loadInstrument();
        if(isNewInstrument && !isRootElement && !isSimpleValidation) {
            var parentMono = loadParent();
            return Mono.zip(instrumentMono, parentMono, this::validateParent);
        }

        return instrumentMono;
    }

    protected Mono<Instrument> loadParent() {
        if(isRootElement) {
            // the tenant or Root element has no parent
            return Mono.empty();
        }
        return dataReader.findByBusinesskey(parentBusinesskey)
                .switchIfEmpty(Mono.error(new MFException(MFMsgKey.UNKNOWN_PARENT_EXCEPTION, domainObjectName+" not saved: unknown parent:"+ parentBusinesskey)));
    }

    protected Flux<Instrument> getAllInstrumentChildsWithType(InstrumentType instrumentType) {
        return getInstrumentChilds(businesskey, EdgeType.TENANTGRAPH, 0)
                .filter(e -> e.getInstrumentType().equals(instrumentType));
    }

    protected Instrument validateParent(Instrument instrument, Instrument parent) {
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

    public Flux<Instrument> getInstrumentChilds(EdgeType edgeType, int pathlength){
        return getInstrumentChilds(businesskey, edgeType, pathlength);
    }

    protected Flux<Instrument> getInstrumentChilds(String businesskey, EdgeType edgeType, int pathlength){
        return instrumentGraphHandler.getInstrumentChildIds(businesskey, edgeType, pathlength)
                .reduce(new ArrayList<String>(), (e1,e2)-> {
                    e1.add(e2);
                    return e1;
                }).flatMapMany(dataReader::findAllById);
    }

    public Flux<String> getAncestorIds() {
        return instrumentGraphHandler.getAncestorIds(businesskey, EdgeType.TENANTGRAPH);
    }

    public Flux<Instrument> listInstrumentChilds(String businesskey) {
        return listInstrumentChilds(getInstrumentById(businesskey, "instrument not found:"+instrumentId));
    }

    public Flux<Instrument> listInstrumentChilds() {
        return listInstrumentChilds(loadInstrument());
    }

    public Flux<Instrument> listFirstLevelInstrumentChilds() {
        return listInstrumentChilds(loadInstrument(), 1);
    }

    public Flux<Instrument> listFirstLevelInstrumentChilds(InstrumentType instrumentType, boolean onlyActive) {
        return filterInstrumentChilds(listInstrumentChilds(instrumentType, onlyActive), true, instrumentType, onlyActive);
    }

    public Flux<Instrument> listActiveInstrumentChilds() {
        return filterInstrumentChilds(listInstrumentChilds(), false, null, true);
    }

    public Flux<Instrument> listInstrumentChilds(InstrumentType instrumentType, boolean onlyActive) {
        return filterInstrumentChilds(listInstrumentChilds(), true, instrumentType, onlyActive);
    }

    private Flux<Instrument> listInstrumentChilds(Mono<Instrument> instrument){
        return listInstrumentChilds(instrument, 0);
    }

    protected Flux<Instrument> listInstrumentChilds(Mono<Instrument> instrument, int pathlength) {
        return instrument.flatMapMany(e->instrumentGraphHandler.getInstrumentChildIds(e.getBusinesskey(), EdgeType.TENANTGRAPH, pathlength))
                .reduce(new ArrayList<String>(), (result, element) -> {
                    result.add(element);
                    return result;
                })
                .flatMapMany(dataReader::findByBusinesskeyIn);
    }

    protected Flux<Instrument> filterActiveInstrumentChilds(Flux<Instrument> childs) {
        return filterInstrumentChilds(childs, false, null, true);
    }

    protected Flux<Instrument> filterInstrumentChilds(Flux<Instrument> childs, boolean filterInstrumentType, InstrumentType instrumentType, boolean onlyActive) {
        Flux<Instrument> instruments = childs;
        if(onlyActive) {
            instruments = childs.filter(Instrument::isIsactive);
        }
        if(filterInstrumentType) {
            instruments = instruments.filter(i->
                    i.getInstrumentType().equals(instrumentType));
        }
        return instruments;
    }
}