package de.hf.myfinance.instruments.service.instrumentgraphhandler;

import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.entities.InstrumentGraphEntry;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithGraph;
import de.hf.myfinance.restmodel.InstrumentType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class InstrumentGraphHandlerBase implements InstrumentGraphHandler{
    final InstrumentGraphRepository instrumentGraphRepository;
    final InstrumentRepository instrumentRepository;

    public InstrumentGraphHandlerBase(InstrumentEnvironmentWithGraph instrumentEnvironment) {
            this.instrumentGraphRepository = instrumentEnvironment.getInstrumentGraphRepository();
            this.instrumentRepository = instrumentEnvironment.getInstrumentRepository();
    }

    @Override
    public void addInstrumentToGraph(final String instrumentId, final String ancestorId, final EdgeType edgeType){
        List<InstrumentGraphEntry> ancestorGraphEntries = instrumentGraphRepository.findByDescendantAndEdgetype(ancestorId, edgeType);
        if(instrumentId!=ancestorId && ancestorGraphEntries.isEmpty()){
            final InstrumentGraphEntry newEntry = new InstrumentGraphEntry(ancestorId, ancestorId, edgeType);
            newEntry.setPathlength(0);
            instrumentGraphRepository.save(newEntry);
            ancestorGraphEntries = instrumentGraphRepository.findByDescendantAndEdgetype(ancestorId, edgeType);
        }
        for (final InstrumentGraphEntry entry : ancestorGraphEntries) {
            final InstrumentGraphEntry newEntry = new InstrumentGraphEntry(entry.getAncestor(), instrumentId, edgeType);
            newEntry.setPathlength(entry.getPathlength()+1);
            instrumentGraphRepository.save(newEntry);
        }
        final InstrumentGraphEntry newEntry = new InstrumentGraphEntry(instrumentId, instrumentId, edgeType);
        newEntry.setPathlength(0);
        instrumentGraphRepository.save(newEntry);
    }

    @Override
    public Optional<String> getRootInstrument(final String instrumentId, final EdgeType edgeType) {
        var instruments = instrumentGraphRepository.findByDescendantAndEdgetype(instrumentId, edgeType);
        if(instruments == null || instruments.isEmpty()) {
            return Optional.empty();
        }
        String rootInstrument = null;
        int maxPathLength = 0;
        for (InstrumentGraphEntry entry : instruments) {
            if(maxPathLength<= entry.getPathlength()) {
                maxPathLength = entry.getPathlength();
                rootInstrument = entry.getAncestor();
            }
        }
        return Optional.of(rootInstrument);
    }

    @Override
    public  List<String> getInstrumentChildIds(final String instrumentId, final EdgeType edgeType){
        return getInstrumentChildIds(instrumentId, edgeType, 0);
    }

    @Override
    public  List<String> getInstrumentChildIds(final String instrumentId, final EdgeType edgeType, int pathlength){
        var childs = instrumentGraphRepository.findByAncestorAndEdgetype(instrumentId, edgeType);
        if(childs==null) {
            return new ArrayList<>();
        }
        if(pathlength>0) {
            return childs.stream().filter(i -> i.getPathlength()==pathlength).map(j->j.getDescendant()).collect(Collectors.toList());
        } else {
            return childs.stream().filter(i -> i.getPathlength()>0).map(j->j.getDescendant()).collect(Collectors.toList());
        }
    }

    @Override
    public List<InstrumentEntity> getInstrumentChilds(final String instrumentId, final EdgeType edgeType, final int pathlength){
        /*Iterable<String>  childIds = getInstrumentChildIds(instrumentId, edgeType, pathlength);
        if(childIds==null) {
            return new ArrayList<>();
        }
        ArrayList<InstrumentEntity> childs = Lists.newArrayList(instrumentRepository.findAllById(childIds));
        return childs;*/
        return null;
    }

    @Override
    public List<InstrumentEntity> getAllInstrumentChilds(final String instrumentId, final EdgeType edgeType){
        return getInstrumentChilds(instrumentId, edgeType, 0);
    }

    protected List<InstrumentEntity> filterInstruments(final List<InstrumentEntity> instruments, final boolean filterInstrumentType, final InstrumentType instrumentType, final boolean onlyActive){
        List<InstrumentEntity> filteredInstruments = instruments;
        if ( instruments!= null && !instruments.isEmpty()) {
            filteredInstruments = instruments.stream().filter(i->(
                    (!filterInstrumentType || i.getInstrumentType().equals(instrumentType))
                    && (!onlyActive || i.isIsactive())
                )
            ).collect(Collectors.toList());
        }
        return filteredInstruments;
    }

    @Override
    public String getAncestorId(final String instrumentId, final EdgeType edgeType) {
        final var ancestors = instrumentGraphRepository.findByDescendantAndEdgetype(instrumentId, edgeType);
        if(ancestors == null || ancestors.isEmpty()) {
            throw new MFException(MFMsgKey.ANCESTOR_DOES_NOT_EXIST_EXCEPTION, "no ancestor for id:"+instrumentId);
        }
        return ancestors.stream().findFirst().get().getAncestor();
    }

    @Override
    public List<InstrumentGraphEntry> getAncestors(final String instrumentId, final EdgeType edgeType) {
        return  instrumentGraphRepository.findByDescendantAndEdgetype(instrumentId, edgeType);
    }
}