package de.hf.myfinance.instruments.persistence.entities;

import javax.persistence.*;

@Entity
@Table(
        name="mf_instrumentgraph")
public class InstrumentGraphEntry implements java.io.Serializable {
    private static final long serialVersionUID = 1L;


    private InstrumentGraphId id;
    private int pathlength;
    private InstrumentEntity ancestor;
    private InstrumentEntity descendant;

    public InstrumentGraphEntry() {
    }

    public InstrumentGraphEntry(int ancestorId, int descendantId, EdgeType edgeType) {
        this.id = new InstrumentGraphId(ancestorId, descendantId, edgeType);
        this.pathlength = pathlength;
    }

    @EmbeddedId
    @AttributeOverrides( {
            @AttributeOverride(name="ancestor", column=@Column(name="ancestor", nullable=false) ),
            @AttributeOverride(name="descendant", column=@Column(name="descendant", nullable=false) ),
            @AttributeOverride(name="edgetype", column=@Column(name="edgetype", nullable=false) ) } )
    public InstrumentGraphId getId() {
        return this.id;
    }
    public void setId(InstrumentGraphId id) {
        this.id = id;
    }


    @Column(name="pathlength", nullable=false)
    public int getPathlength() {
        return this.pathlength;
    }
    public void setPathlength(int pathlength) {
        this.pathlength = pathlength;
    }

    @OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "ancestor", referencedColumnName = "instrumentid", insertable = false, updatable = false)
    public InstrumentEntity getAncestor() {
        return this.ancestor;
    }
    public void setAncestor(InstrumentEntity i) {
    }

    @OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "descendant", referencedColumnName = "instrumentid", insertable = false, updatable = false)
    public InstrumentEntity getDescendant() {
        return this.descendant;
    }
    public void setDescendant(InstrumentEntity i) {}
}

