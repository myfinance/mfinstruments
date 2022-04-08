package de.hf.myfinance.instruments.persistence.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "instrumentgraph")
@CompoundIndex(name = "graphid", unique = true, def = "{'ancestor': 1, 'descendant' : 1, 'edgetype' : 1}")
public class InstrumentGraphEntry implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String instrumentGraphid;
    @Version
    private Integer version;

    private String ancestor;
    private String descendant;
    private EdgeType edgetype;
    private int pathlength;

    public InstrumentGraphEntry() {
    }

    public InstrumentGraphEntry(String ancestorId, String descendantId, EdgeType edgeType) {
        this.ancestor = ancestor;
        this.descendant = descendant;
        this.edgetype = edgetype;
        this.pathlength = pathlength;
    }

    public String getInstrumentGraphid() {
        return instrumentGraphid;
    }

    public void setInstrumentGraphid(String instrumentGraphid) {
        this.instrumentGraphid = instrumentGraphid;
    }

    public int getPathlength() {
        return this.pathlength;
    }
    public void setPathlength(int pathlength){
        this.pathlength = pathlength;
    }

    public String getAncestor() {
        return ancestor;
    }
    public void setAncestor(String ancestor) {
        this.ancestor = ancestor;
    }

    public String getDescendant() {
        return descendant;
    }
    public void setDescendant(String descendant) {
        this.descendant = descendant;
    }

    public EdgeType getEdgetype() {
        return edgetype;
    }
    public void setEdgetype(EdgeType edgetype) {
        this.edgetype = edgetype;
    }

    public Integer getVersion() {
        return version;
    }
    public void setVersion(Integer version) {
        this.version = version;
    }
}

