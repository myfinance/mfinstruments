package de.hf.myfinance.instruments.service.instrumentgraphhandler;

import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.exception.MFMsgKey;
import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.entities.InstrumentGraphEntry;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.restmodel.InstrumentType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class InstrumentGraphHandlerBase implements InstrumentGraphHandler{
    final InstrumentGraphRepository instrumentGraphRepository;

    public InstrumentGraphHandlerBase(InstrumentGraphRepository instrumentGraphRepository) {
            this.instrumentGraphRepository = instrumentGraphRepository;
    }

    @Override
    public void addInstrumentToGraph(final int instrumentId, final int ancestorId, final EdgeType edgeType){
        List<InstrumentGraphEntry> ancestorGraphEntries = instrumentGraphRepository.findByDescendantAndEdgetype(ancestorId, edgeType);
        if(instrumentId!=ancestorId && ancestorGraphEntries.isEmpty()){
            final InstrumentGraphEntry newEntry = new InstrumentGraphEntry(ancestorId, ancestorId, edgeType);
            newEntry.setPathlength(0);
            instrumentGraphRepository.save(newEntry);
            ancestorGraphEntries = instrumentGraphRepository.findByDescendantAndEdgetype(ancestorId, edgeType);
        }
        for (final InstrumentGraphEntry entry : ancestorGraphEntries) {
            final InstrumentGraphEntry newEntry = new InstrumentGraphEntry(entry.getId().getAncestor(), instrumentId, edgeType);
            newEntry.setPathlength(entry.getPathlength()+1);
            instrumentGraphRepository.save(newEntry);
        }
        final InstrumentGraphEntry newEntry = new InstrumentGraphEntry(instrumentId, instrumentId, edgeType);
        newEntry.setPathlength(0);
        instrumentGraphRepository.save(newEntry);
    }

    @Override
    public Optional<InstrumentEntity> getRootInstrument(final int instrumentId, final EdgeType edgeType) {
        var instruments = instrumentGraphRepository.findByDescendantAndEdgetype(instrumentId, edgeType);
        if(instruments == null || instruments.isEmpty()) {
            return Optional.empty();
        }
        InstrumentEntity rootInstrument = null;
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
    public List<InstrumentEntity> getInstrumentChilds(final int instrumentId, final EdgeType edgeType, final int pathlength){
        List<InstrumentEntity>  childs = instrumentGraphRepository.getInstrumentChilds(instrumentId, edgeType, pathlength);
        if(childs==null) {
            childs = new ArrayList<>();
        }
        return childs;
    }

    @Override
    public List<InstrumentEntity> getAllInstrumentChilds(final int instrumentId, final EdgeType edgeType){
        List<InstrumentEntity>  childs = instrumentGraphRepository.getInstrumentChilds(instrumentId, edgeType);
        if(childs==null) {
            childs = new ArrayList<>();
        }
        return childs;
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
    public int getAncestorId(final int instrumentId, final EdgeType edgeType) {
        final var ancestors = instrumentGraphRepository.findByDescendantAndEdgetype(instrumentId, edgeType);
        if(ancestors == null || ancestors.isEmpty()) {
            throw new MFException(MFMsgKey.ANCESTOR_DOES_NOT_EXIST_EXCEPTION, "no ancestor for id:"+instrumentId);
        }
        return ancestors.stream().findFirst().get().getId().getAncestor();
    }

    @Override
    public List<InstrumentGraphEntry> getAncestors(final int instrumentId, final EdgeType edgeType) {
        return  instrumentGraphRepository.findByDescendantAndEdgetype(instrumentId, edgeType);
    }
}