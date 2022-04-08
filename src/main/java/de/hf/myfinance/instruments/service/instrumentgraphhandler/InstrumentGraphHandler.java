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
    void addInstrumentToGraph(String instrumentId, String ancestorId, EdgeType edgeType);
    /** calls addInstrumentToGraph(int instrumentId, int ancestorId, EdgeType edgeType) with EdgeType =  TenantGraph */
    void addInstrumentToGraph(String instrumentId, String ancestorId);

    /**
     * get the id of the rootinstrument in the graph 
     * @param instrumentId the id of the child instrument where there the root is requested for
     * @param edgeType the edgetype that describes the relation between parent and child e.G. TenantGraph
     * @return the optional of the id of the root instrument
     */
    Optional<String> getRootInstrument(String instrumentId, EdgeType edgeType);
    /** calls getRootInstrument(int instrumentId, EdgeType edgeType) with EdgeType =  TenantGraph */
    Optional<String> getRootInstrument(String instrumentId);

    List<String> getInstrumentChildIds(final String instrumentId, final EdgeType edgeType);
    List<String> getInstrumentChildIds(final String instrumentId, final EdgeType edgeType, int pathlength);


    /**
     * get the childs of an instrument with an spezific pathlegth in the graph
     * @param instrumentId - the id of the instrument for which the childs are requested
     * @param edgeType - the edgetype that describes the relation between parent and child e.G. TenantGraph
     * @param pathlength - the pathlength
     * @return the childs of the instrument with an spezific pathlegth in the graph
     */
    List<InstrumentEntity> getInstrumentChilds(String instrumentId, EdgeType edgeType, int pathlength);
    /** calls getInstrumentChilds(int instrumentId, EdgeType edgeType, int pathlength) with pathleght = 1 */
    List<InstrumentEntity> getInstrumentFirstLevelChilds(String instrumentId, EdgeType edgeType);
    /**  calls getInstrumentChilds(int instrumentId, EdgeType edgeType, int pathlength) with pathleght = 1 and EdgeType =  TenantGraph */
    List<InstrumentEntity> getInstrumentFirstLevelChilds(String instrumentId);
    /** calls getInstrumentChilds(int instrumentId, EdgeType edgeType, int pathlength) EdgeType =  TenantGraph*/
    List<InstrumentEntity> getInstrumentChilds(String instrumentId, int pathlength);
    /** calls getInstrumentChilds(int instrumentId, EdgeType edgeType, int pathlength) with pathlegth = 1 edgetype=TenantGraph and filters for the instrumenttype*/
    List<InstrumentEntity> getInstrumentFirstLevelChildsWithType(String instrumentId, final InstrumentType instrumentType, final boolean onlyActive);
    /** calls getInstrumentFirstLevelChildsWithType with onlyactive=true and returns the first match*/
    InstrumentEntity getFirstLevelChildsPerTypeFirstmatch(String instrumentId, InstrumentType instrumentType);

    /**
     * get all childs of an instrument no matter the pathlegth in the graph
     * @param instrumentId - the id of the instrument for which the childs are requested
     * @param edgeType - the edgetype that describes the relation between parent and child e.G. TenantGraph
     * @return all childs of the instrument 
     */
    List<InstrumentEntity> getAllInstrumentChilds(String instrumentId, EdgeType edgeType);
    /** calls  getAllInstrumentChilds(int instrumentId, EdgeType edgeType) and filters for active instruments in case onlyActive=true */
    List<InstrumentEntity> getAllInstrumentChilds(String instrumentId, EdgeType edgeType, boolean onlyActive);
    /** calls  getAllInstrumentChilds(int instrumentId, EdgeType edgeType) with EdgeType = TenantGraph and filters for active instruments in case onlyActive=true */
    List<InstrumentEntity> getAllInstrumentChilds(String instrumentId, boolean onlyActive);
    /**  calls getAllInstrumentChilds(int instrumentId, EdgeType edgeType) with EdgeType = TenantGraph */
    List<InstrumentEntity> getAllInstrumentChilds(String instrumentId);
    /** calls getAllInstrumentChilds(int instrumentId, EdgeType edgeType) with EdgeType = TenantGraph and filters for instrument with type=instrumentType and for active instruments in case onlyActive=true */
    List<InstrumentEntity> getAllInstrumentChilds(final String instrumentId, final InstrumentType instrumentType, final boolean onlyActive);
    /**  calls getAllInstrumentChilds(int instrumentId, EdgeType edgeType) with EdgeType = TenantGraph  and filters for instrument with type=instrumentType */
    List<InstrumentEntity> getAllInstrumentChildsWithType(String instrumentId, InstrumentType instrumentType);
        
    /**
     * 
     * @param instrumentId - the id of the instrument for which the childs are requested
     * @param edgeType - the edgetype that describes the relation between parent and child e.G. TenantGraph
     * @return the id of the ancestor of the instrument
     */
    String getAncestorId(String instrumentId, EdgeType edgeType);
    /** calls getAncestorId(int instrumentId, EdgeType edgeType) with edgetype=TenantGraph */
    String getAncestorId(String instrumentId);

    List<InstrumentGraphEntry> getAncestors(final String instrumentId, final EdgeType edgeType);
    
}