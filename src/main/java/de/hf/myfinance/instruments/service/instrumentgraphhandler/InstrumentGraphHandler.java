package de.hf.myfinance.instruments.service.instrumentgraphhandler;

import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.entities.InstrumentGraphEntry;
import de.hf.myfinance.restmodel.InstrumentType;

import java.util.List;
import java.util.Optional;

public interface InstrumentGraphHandler {
    /**
     * add an instrument to the graph
     * @param instrumentId the instrumentId of the instrument
     * @param ancestorId the id of the parent of the instrument
     * @param edgeType the edgetype that describes the relation between parent and child e.G. TenantGraph
     */
    void addInstrumentToGraph(int instrumentId, int ancestorId, EdgeType edgeType);
    /** calls addInstrumentToGraph(int instrumentId, int ancestorId, EdgeType edgeType) with EdgeType =  TenantGraph */
    void addInstrumentToGraph(int instrumentId, int ancestorId);

    /**
     * get the id of the rootinstrument in the graph 
     * @param instrumentId the id of the child instrument where there the root is requested for
     * @param edgeType the edgetype that describes the relation between parent and child e.G. TenantGraph
     * @return the optional of the id of the root instrument
     */
    Optional<InstrumentEntity> getRootInstrument(int instrumentId, EdgeType edgeType);
    /** calls getRootInstrument(int instrumentId, EdgeType edgeType) with EdgeType =  TenantGraph */
    Optional<InstrumentEntity> getRootInstrument(int instrumentId);

    /**
     * get the childs of an instrument with an spezific pathlegth in the graph
     * @param instrumentId - the id of the instrument for which the childs are requested
     * @param edgeType - the edgetype that describes the relation between parent and child e.G. TenantGraph
     * @param pathlength - the pathlength
     * @return the childs of the instrument with an spezific pathlegth in the graph
     */
    List<InstrumentEntity> getInstrumentChilds(int instrumentId, EdgeType edgeType, int pathlength);
    /** calls getInstrumentChilds(int instrumentId, EdgeType edgeType, int pathlength) with pathleght = 1 */
    List<InstrumentEntity> getInstrumentFirstLevelChilds(int instrumentId, EdgeType edgeType);
    /**  calls getInstrumentChilds(int instrumentId, EdgeType edgeType, int pathlength) with pathleght = 1 and EdgeType =  TenantGraph */
    List<InstrumentEntity> getInstrumentFirstLevelChilds(int instrumentId);
    /** calls getInstrumentChilds(int instrumentId, EdgeType edgeType, int pathlength) EdgeType =  TenantGraph*/
    List<InstrumentEntity> getInstrumentChilds(int instrumentId, int pathlength);
    /** calls getInstrumentChilds(int instrumentId, EdgeType edgeType, int pathlength) with pathlegth = 1 edgetype=TenantGraph and filters for the instrumenttype*/
    List<InstrumentEntity> getInstrumentFirstLevelChildsWithType(int instrumentId, final InstrumentType instrumentType, final boolean onlyActive);
    /** calls getInstrumentFirstLevelChildsWithType with onlyactive=true and returns the first match*/
    InstrumentEntity getFirstLevelChildsPerTypeFirstmatch(int instrumentId, InstrumentType instrumentType);

    /**
     * get all childs of an instrument no matter the pathlegth in the graph
     * @param instrumentId - the id of the instrument for which the childs are requested
     * @param edgeType - the edgetype that describes the relation between parent and child e.G. TenantGraph
     * @return all childs of the instrument 
     */
    List<InstrumentEntity> getAllInstrumentChilds(int instrumentId, EdgeType edgeType);
    /** calls  getAllInstrumentChilds(int instrumentId, EdgeType edgeType) and filters for active instruments in case onlyActive=true */
    List<InstrumentEntity> getAllInstrumentChilds(int instrumentId, EdgeType edgeType, boolean onlyActive);
    /** calls  getAllInstrumentChilds(int instrumentId, EdgeType edgeType) with EdgeType = TenantGraph and filters for active instruments in case onlyActive=true */
    List<InstrumentEntity> getAllInstrumentChilds(int instrumentId, boolean onlyActive);
    /**  calls getAllInstrumentChilds(int instrumentId, EdgeType edgeType) with EdgeType = TenantGraph */
    List<InstrumentEntity> getAllInstrumentChilds(int instrumentId);
    /** calls getAllInstrumentChilds(int instrumentId, EdgeType edgeType) with EdgeType = TenantGraph and filters for instrument with type=instrumentType and for active instruments in case onlyActive=true */
    List<InstrumentEntity> getAllInstrumentChilds(final int instrumentId, final InstrumentType instrumentType, final boolean onlyActive);
    /**  calls getAllInstrumentChilds(int instrumentId, EdgeType edgeType) with EdgeType = TenantGraph  and filters for instrument with type=instrumentType */
    List<InstrumentEntity> getAllInstrumentChildsWithType(int instrumentId, InstrumentType instrumentType);
        
    /**
     * 
     * @param instrumentId - the id of the instrument for which the childs are requested
     * @param edgeType - the edgetype that describes the relation between parent and child e.G. TenantGraph
     * @return the id of the ancestor of the instrument
     */
    int getAncestorId(int instrumentId, EdgeType edgeType);
    /** calls getAncestorId(int instrumentId, EdgeType edgeType) with edgetype=TenantGraph */
    int getAncestorId(int instrumentId);

    List<InstrumentGraphEntry> getAncestors(final int instrumentId, final EdgeType edgeType);
    
}