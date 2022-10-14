package de.hf.myfinance.instruments;

import de.hf.myfinance.event.Event;
import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SaveInstrumentTreeProcessorTest extends EventProcessorTestBase {

    @Autowired
    @Qualifier("saveInstrumentTreeProcessor")
    protected Consumer<Event<Integer, Instrument>> saveInstrumentTreeProcessor;

    @Test
    void createTenant() {
        var businessKey = "aTest@6";
        var desc = "aTest";
        var newInstrument = new Instrument(desc, InstrumentType.TENANT);
        newInstrument.setBusinesskey(businessKey);
        Event creatEvent = new Event(Event.Type.CREATE, businessKey, newInstrument);
        saveInstrumentTreeProcessor.accept(creatEvent);

        var instrumentGraph = instrumentGraphRepository.findAll().collectList().block();
        assertEquals(1, instrumentGraph.size());

        var instrumentGraphEntry = instrumentGraph.get(0);
        assertEquals(businessKey, instrumentGraphEntry.getAncestor());
        assertEquals(businessKey, instrumentGraphEntry.getDescendant());
        assertEquals(0, instrumentGraphEntry.getPathlength());
        assertEquals(EdgeType.TENANTGRAPH, instrumentGraphEntry.getEdgetype());
    }

    @Test
    void createFullTenant() {
        var businessKey = "aTest@6";
        var desc = "aTest";
        var tenant = new Instrument(desc, InstrumentType.TENANT);
        tenant.setBusinesskey(businessKey);
        Event creatEvent = new Event(Event.Type.CREATE, businessKey, tenant);
        saveInstrumentTreeProcessor.accept(creatEvent);

        var budgetPfdesc = "bgtPf_"+desc;
        var budgetPfKey = budgetPfdesc+"@23";
        var budgetPf = new Instrument(budgetPfdesc, InstrumentType.BUDGETPORTFOLIO);
        budgetPf.setBusinesskey(budgetPfKey);
        budgetPf.setParentBusinesskey(businessKey);
        creatEvent = new Event(Event.Type.CREATE, budgetPfKey, budgetPf);
        saveInstrumentTreeProcessor.accept(creatEvent);

        var instrumentGraph = instrumentGraphRepository.findAll().collectList().block();
        assertEquals(3, instrumentGraph.size());

        var instrumentGraphEntry = instrumentGraph.get(0);
        assertEquals(businessKey, instrumentGraphEntry.getAncestor());
        assertEquals(businessKey, instrumentGraphEntry.getDescendant());
        assertEquals(0, instrumentGraphEntry.getPathlength());

        instrumentGraphEntry = instrumentGraph.get(1);
        assertEquals(businessKey, instrumentGraphEntry.getAncestor());
        assertEquals(budgetPfKey, instrumentGraphEntry.getDescendant());
        assertEquals(1, instrumentGraphEntry.getPathlength());

        instrumentGraphEntry = instrumentGraph.get(2);
        assertEquals(budgetPfKey, instrumentGraphEntry.getAncestor());
        assertEquals(budgetPfKey, instrumentGraphEntry.getDescendant());
        assertEquals(0, instrumentGraphEntry.getPathlength());

        var bgtGrpdesc = "bgtGrp_"+budgetPfdesc;
        var bgtGrpKey = bgtGrpdesc+"@10";
        var bgtGrp = new Instrument(bgtGrpdesc, InstrumentType.BUDGETGROUP);
        bgtGrp.setBusinesskey(bgtGrpKey);
        bgtGrp.setParentBusinesskey(budgetPfKey);
        creatEvent = new Event(Event.Type.CREATE, bgtGrpKey, bgtGrp);
        saveInstrumentTreeProcessor.accept(creatEvent);

        instrumentGraph = instrumentGraphRepository.findAll().collectList().block();
        assertEquals(6, instrumentGraph.size());

        instrumentGraphEntry = instrumentGraph.get(0);
        assertEquals(businessKey, instrumentGraphEntry.getAncestor());
        assertEquals(businessKey, instrumentGraphEntry.getDescendant());
        assertEquals(0, instrumentGraphEntry.getPathlength());

        instrumentGraphEntry = instrumentGraph.get(1);
        assertEquals(businessKey, instrumentGraphEntry.getAncestor());
        assertEquals(budgetPfKey, instrumentGraphEntry.getDescendant());
        assertEquals(1, instrumentGraphEntry.getPathlength());

        instrumentGraphEntry = instrumentGraph.get(2);
        assertEquals(budgetPfKey, instrumentGraphEntry.getAncestor());
        assertEquals(budgetPfKey, instrumentGraphEntry.getDescendant());
        assertEquals(0, instrumentGraphEntry.getPathlength());

        instrumentGraphEntry = instrumentGraph.get(3);
        assertEquals(businessKey, instrumentGraphEntry.getAncestor());
        assertEquals(bgtGrpKey, instrumentGraphEntry.getDescendant());
        assertEquals(2, instrumentGraphEntry.getPathlength());

        instrumentGraphEntry = instrumentGraph.get(4);
        assertEquals(budgetPfKey, instrumentGraphEntry.getAncestor());
        assertEquals(bgtGrpKey, instrumentGraphEntry.getDescendant());
        assertEquals(1, instrumentGraphEntry.getPathlength());

        instrumentGraphEntry = instrumentGraph.get(5);
        assertEquals(bgtGrpKey, instrumentGraphEntry.getAncestor());
        assertEquals(bgtGrpKey, instrumentGraphEntry.getDescendant());
        assertEquals(0, instrumentGraphEntry.getPathlength());

        var bgtdesc = "incomeBgt_"+bgtGrpdesc;
        var bgtKey = bgtdesc+"@10";
        var bgt = new Instrument(bgtGrpdesc, InstrumentType.BUDGET);
        bgt.setBusinesskey(bgtKey);
        bgt.setParentBusinesskey(bgtGrpKey);
        creatEvent = new Event(Event.Type.CREATE, bgtKey, bgt);
        saveInstrumentTreeProcessor.accept(creatEvent);

        instrumentGraph = instrumentGraphRepository.findAll().collectList().block();
        assertEquals(10, instrumentGraph.size());

        instrumentGraphEntry = instrumentGraph.get(7);
        assertEquals(businessKey, instrumentGraphEntry.getAncestor());
        assertEquals(bgtKey, instrumentGraphEntry.getDescendant());
        assertEquals(3, instrumentGraphEntry.getPathlength());
        assertEquals(EdgeType.TENANTGRAPH, instrumentGraphEntry.getEdgetype());

        instrumentGraphEntry = instrumentGraph.get(6);
        assertEquals(budgetPfKey, instrumentGraphEntry.getAncestor());
        assertEquals(bgtKey, instrumentGraphEntry.getDescendant());
        assertEquals(2, instrumentGraphEntry.getPathlength());
        assertEquals(EdgeType.TENANTGRAPH, instrumentGraphEntry.getEdgetype());

        instrumentGraphEntry = instrumentGraph.get(8);
        assertEquals(bgtGrpKey, instrumentGraphEntry.getAncestor());
        assertEquals(bgtKey, instrumentGraphEntry.getDescendant());
        assertEquals(1, instrumentGraphEntry.getPathlength());
        assertEquals(EdgeType.TENANTGRAPH, instrumentGraphEntry.getEdgetype());

        instrumentGraphEntry = instrumentGraph.get(9);
        assertEquals(bgtKey, instrumentGraphEntry.getAncestor());
        assertEquals(bgtKey, instrumentGraphEntry.getDescendant());
        assertEquals(0, instrumentGraphEntry.getPathlength());
        assertEquals(EdgeType.TENANTGRAPH, instrumentGraphEntry.getEdgetype());

        var accPfdesc = "accPf_"+desc;
        var accPfKey = accPfdesc+"@8";
        var accPf = new Instrument(accPfdesc, InstrumentType.ACCOUNTPORTFOLIO);
        accPf.setBusinesskey(accPfKey);
        accPf.setParentBusinesskey(businessKey);
        creatEvent = new Event(Event.Type.CREATE, accPfKey, accPf);
        saveInstrumentTreeProcessor.accept(creatEvent);

        instrumentGraph = instrumentGraphRepository.findAll().collectList().block();
        assertEquals(12, instrumentGraph.size());
    }
}
