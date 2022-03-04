package de.hf.myfinance.instruments.service.instrumentgraphhandler;


import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.restmodel.InstrumentType;

import java.util.List;
import java.util.Optional;

public class InstrumentGraphHandlerImpl extends InstrumentGraphHandlerBase{

    public InstrumentGraphHandlerImpl(final InstrumentGraphRepository instrumentGraphRepository) {
        super(instrumentGraphRepository);
    }

    @Override
    public void addInstrumentToGraph(final int instrumentId, final int ancestorId){
        addInstrumentToGraph(instrumentId, ancestorId, EdgeType.TENANTGRAPH);
    }

    @Override
    public Optional<InstrumentEntity> getRootInstrument(final int instrumentId) {
        return getRootInstrument(instrumentId, EdgeType.TENANTGRAPH);
    }

    @Override
    public List<InstrumentEntity> getInstrumentFirstLevelChilds(final int instrumentId){
        return getInstrumentFirstLevelChilds(instrumentId, EdgeType.TENANTGRAPH);
    }

    @Override
    public List<InstrumentEntity> getInstrumentFirstLevelChilds(final int instrumentId, final EdgeType edgeType){
        return getInstrumentChilds(instrumentId, edgeType, 1);
    }

    @Override
    public List<InstrumentEntity> getInstrumentFirstLevelChildsWithType(final int instrumentId, final InstrumentType instrumentType, final boolean onlyActive){
        return filterInstruments(getInstrumentChilds(instrumentId, EdgeType.TENANTGRAPH, 1), true, instrumentType, onlyActive);
    }

    @Override
    public List<InstrumentEntity> getInstrumentChilds(final int instrumentId, final int pathlength){
        return getInstrumentChilds(instrumentId, EdgeType.TENANTGRAPH, pathlength);
    }

    @Override
    public List<InstrumentEntity> getAllInstrumentChilds(final int instrumentId, final boolean onlyActive) {
        return getAllInstrumentChilds(instrumentId, EdgeType.TENANTGRAPH, onlyActive);
    }

    @Override
    public List<InstrumentEntity> getAllInstrumentChilds(final int instrumentId, final EdgeType edgeType, final boolean onlyActive){
        return filterInstruments(getAllInstrumentChilds(instrumentId, edgeType), false, InstrumentType.UNKNOWN, onlyActive);
    }

    @Override
    public List<InstrumentEntity> getAllInstrumentChilds(final int instrumentId, final InstrumentType instrumentType, final boolean onlyActive){
        return filterInstruments(getAllInstrumentChilds(instrumentId, EdgeType.TENANTGRAPH), false, instrumentType, onlyActive);
    }

    @Override
    public List<InstrumentEntity> getAllInstrumentChildsWithType(int instrumentId, InstrumentType instrumentType) {
        return filterInstruments(getAllInstrumentChilds(instrumentId, EdgeType.TENANTGRAPH), true, instrumentType, false);
    }


    @Override
    public List<InstrumentEntity> getAllInstrumentChilds(final int instrumentId){
        return getAllInstrumentChilds(instrumentId, EdgeType.TENANTGRAPH);
    }

    @Override
    public InstrumentEntity getFirstLevelChildsPerTypeFirstmatch(final int instrumentId, final InstrumentType instrumentType) {
        final var instruments = getInstrumentFirstLevelChildsWithType(instrumentId, instrumentType, true);
        if(instruments == null || instruments.isEmpty()) return null;
        return instruments.get(0);
    }
    
    @Override
    public int getAncestorId(final int instrumentId) {
        return getAncestorId(instrumentId, EdgeType.TENANTGRAPH);
    }

}