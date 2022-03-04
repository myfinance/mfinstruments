package de.hf.myfinance.instruments.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;

@Embeddable
public class InstrumentGraphId  implements java.io.Serializable {
    private static final long serialVersionUID = 1L;


    private int ancestor;
    private int descendant;
    private EdgeType edgetype;

    public InstrumentGraphId() {
    }

    public InstrumentGraphId(int ancestor, int descendant, EdgeType edgetype) {
        this.ancestor = ancestor;
        this.descendant = descendant;
        this.edgetype = edgetype;
    }


    @Column(name="ancestor", nullable=false)
    public int getAncestor() {
        return this.ancestor;
    }

    public void setAncestor(int ancestor) {
        this.ancestor = ancestor;
    }

    @Column(name="descendant", nullable=false)
    public int getDescendant() {
        return this.descendant;
    }

    public void setDescendant(int descendant) {
        this.descendant = descendant;
    }

    @Enumerated
    @Column(name="edgetype", nullable=false)
    public EdgeType getEdgetype() {
        return this.edgetype;
    }

    public void setEdgetype(EdgeType edgetype) {
        this.edgetype = edgetype;
    }


    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( (other == null ) ) return false;
        if ( !(other instanceof InstrumentGraphId) ) return false;
        InstrumentGraphId castOther = ( InstrumentGraphId ) other;

        return (this.getAncestor()==castOther.getAncestor())
                && (this.getDescendant()==castOther.getDescendant())
                && (this.getEdgetype()==castOther.getEdgetype());
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result + this.getAncestor();
        result = 37 * result + this.getDescendant();
        result = 37 * result + this.getEdgetype().hashCode();
        return result;
    }


}

