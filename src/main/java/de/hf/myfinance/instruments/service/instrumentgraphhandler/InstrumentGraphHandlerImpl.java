package de.hf.myfinance.instruments.service.instrumentgraphhandler;


import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithGraph;
import de.hf.myfinance.restmodel.InstrumentType;

import java.util.List;
import java.util.Optional;

public class InstrumentGraphHandlerImpl extends InstrumentGraphHandlerBase{

    public InstrumentGraphHandlerImpl(final InstrumentEnvironmentWithGraph instrumentEnvironment) {
        super(instrumentEnvironment);
    }

    @Override
    public void addInstrumentToGraph(final String instrumentId, final String ancestorId){
        addInstrumentToGraph(instrumentId, ancestorId, EdgeType.TENANTGRAPH);
    }

    @Override
    public Optional<String> getRootInstrument(final String instrumentId) {
        return getRootInstrument(instrumentId, EdgeType.TENANTGRAPH);
    }

    @Override
    public List<InstrumentEntity> getInstrumentFirstLevelChilds(final String instrumentId){
        return getInstrumentFirstLevelChilds(instrumentId, EdgeType.TENANTGRAPH);
    }

    @Override
    public List<InstrumentEntity> getInstrumentFirstLevelChilds(final String instrumentId, final EdgeType edgeType){
        return getInstrumentChilds(instrumentId, edgeType, 1);
    }

    @Override
    public List<InstrumentEntity> getInstrumentFirstLevelChildsWithType(final String instrumentId, final InstrumentType instrumentType, final boolean onlyActive){
        return filterInstruments(getInstrumentChilds(instrumentId, EdgeType.TENANTGRAPH, 1), true, instrumentType, onlyActive);
    }

    @Override
    public List<InstrumentEntity> getInstrumentChilds(final String instrumentId, final int pathlength){
        return getInstrumentChilds(instrumentId, EdgeType.TENANTGRAPH, pathlength);
    }

    @Override
    public List<InstrumentEntity> getAllInstrumentChilds(final String instrumentId, final boolean onlyActive) {
        return getAllInstrumentChilds(instrumentId, EdgeType.TENANTGRAPH, onlyActive);
    }

    @Override
    public List<InstrumentEntity> getAllInstrumentChilds(final String instrumentId, final EdgeType edgeType, final boolean onlyActive){
        return filterInstruments(getAllInstrumentChilds(instrumentId, edgeType), false, InstrumentType.UNKNOWN, onlyActive);
    }

    @Override
    public List<InstrumentEntity> getAllInstrumentChilds(final String instrumentId, final InstrumentType instrumentType, final boolean onlyActive){
        return filterInstruments(getAllInstrumentChilds(instrumentId, EdgeType.TENANTGRAPH), false, instrumentType, onlyActive);
    }

    @Override
    public List<InstrumentEntity> getAllInstrumentChildsWithType(String instrumentId, InstrumentType instrumentType) {
        return filterInstruments(getAllInstrumentChilds(instrumentId, EdgeType.TENANTGRAPH), true, instrumentType, false);
    }


    @Override
    public List<InstrumentEntity> getAllInstrumentChilds(final String instrumentId){
        return getAllInstrumentChilds(instrumentId, EdgeType.TENANTGRAPH);
    }

    @Override
    public InstrumentEntity getFirstLevelChildsPerTypeFirstmatch(final String instrumentId, final InstrumentType instrumentType) {
        final var instruments = getInstrumentFirstLevelChildsWithType(instrumentId, instrumentType, true);
        if(instruments == null || instruments.isEmpty()) return null;
        return instruments.get(0);
    }
    
    @Override
    public String getAncestorId(final String instrumentId) {
        return getAncestorId(instrumentId, EdgeType.TENANTGRAPH);
    }

}