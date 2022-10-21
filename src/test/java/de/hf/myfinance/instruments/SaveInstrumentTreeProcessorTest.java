package de.hf.myfinance.instruments;

import de.hf.myfinance.event.Event;
import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.entities.InstrumentGraphEntry;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SaveInstrumentTreeProcessorTest extends EventProcessorTestBase {

    @Autowired
    @Qualifier("saveInstrumentTreeProcessor")
    protected Consumer<Event<String, Instrument>> saveInstrumentTreeProcessor;

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

        assertTrue(existsInGraph(instrumentGraph, businessKey, businessKey, 0));
        assertTrue(existsInGraph(instrumentGraph, budgetPfKey, businessKey, 1));
        assertTrue(existsInGraph(instrumentGraph, budgetPfKey, budgetPfKey, 0));

        var bgtGrpdesc = "bgtGrp_"+budgetPfdesc;
        var bgtGrpKey = bgtGrpdesc+"@10";
        var bgtGrp = new Instrument(bgtGrpdesc, InstrumentType.BUDGETGROUP);
        bgtGrp.setBusinesskey(bgtGrpKey);
        bgtGrp.setParentBusinesskey(budgetPfKey);
        creatEvent = new Event(Event.Type.CREATE, bgtGrpKey, bgtGrp);
        saveInstrumentTreeProcessor.accept(creatEvent);

        instrumentGraph = instrumentGraphRepository.findAll().collectList().block();
        assertEquals(6, instrumentGraph.size());

        assertTrue(existsInGraph(instrumentGraph, businessKey, businessKey, 0));
        assertTrue(existsInGraph(instrumentGraph, budgetPfKey, businessKey, 1));
        assertTrue(existsInGraph(instrumentGraph, budgetPfKey, budgetPfKey, 0));
        assertTrue(existsInGraph(instrumentGraph, bgtGrpKey, businessKey, 2));
        assertTrue(existsInGraph(instrumentGraph, bgtGrpKey, budgetPfKey, 1));
        assertTrue(existsInGraph(instrumentGraph, bgtGrpKey, bgtGrpKey, 0));

        var bgtdesc = "incomeBgt_"+bgtGrpdesc;
        var bgtKey = bgtdesc+"@10";
        var bgt = new Instrument(bgtGrpdesc, InstrumentType.BUDGET);
        bgt.setBusinesskey(bgtKey);
        bgt.setParentBusinesskey(bgtGrpKey);
        creatEvent = new Event(Event.Type.CREATE, bgtKey, bgt);
        saveInstrumentTreeProcessor.accept(creatEvent);

        instrumentGraph = instrumentGraphRepository.findAll().collectList().block();
        assertEquals(10, instrumentGraph.size());

        assertTrue(existsInGraph(instrumentGraph, businessKey, businessKey, 0));
        assertTrue(existsInGraph(instrumentGraph, budgetPfKey, businessKey, 1));
        assertTrue(existsInGraph(instrumentGraph, budgetPfKey, budgetPfKey, 0));
        assertTrue(existsInGraph(instrumentGraph, bgtGrpKey, businessKey, 2));
        assertTrue(existsInGraph(instrumentGraph, bgtGrpKey, budgetPfKey, 1));
        assertTrue(existsInGraph(instrumentGraph, bgtGrpKey, bgtGrpKey, 0));

        assertTrue(existsInGraph(instrumentGraph, bgtKey, businessKey, 3));
        assertTrue(existsInGraph(instrumentGraph, bgtKey, budgetPfKey, 2));
        assertTrue(existsInGraph(instrumentGraph, bgtKey, bgtGrpKey, 1));
        assertTrue(existsInGraph(instrumentGraph, bgtKey, bgtKey, 0));


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

    @Test
    void createTenantDuplicate() {
        var businessKey = "aTest@6";
        var desc = "aTest";
        var newInstrument = new Instrument(desc, InstrumentType.TENANT);
        newInstrument.setBusinesskey(businessKey);
        Event createTenantEvent = new Event(Event.Type.CREATE, businessKey, newInstrument);
        Event createTenantEventDuplicate = new Event(Event.Type.CREATE, businessKey, newInstrument);
        saveInstrumentTreeProcessor.accept(createTenantEvent);

        var budgetPfdesc = "bgtPf_"+desc;
        var budgetPfKey = budgetPfdesc+"@23";
        var budgetPf = new Instrument(budgetPfdesc, InstrumentType.BUDGETPORTFOLIO);
        budgetPf.setBusinesskey(budgetPfKey);
        budgetPf.setParentBusinesskey(businessKey);
        var createBudgetPfEvent = new Event(Event.Type.CREATE, budgetPfKey, budgetPf);
        var createBudgetPfEventDuplicate = new Event(Event.Type.CREATE, budgetPfKey, budgetPf);
        saveInstrumentTreeProcessor.accept(createBudgetPfEvent);

        var bgtGrpdesc = "bgtGrp_"+budgetPfdesc;
        var bgtGrpKey = bgtGrpdesc+"@10";
        var bgtGrp = new Instrument(bgtGrpdesc, InstrumentType.BUDGETGROUP);
        bgtGrp.setBusinesskey(bgtGrpKey);
        bgtGrp.setParentBusinesskey(budgetPfKey);
        var createBgtgrpEvent = new Event(Event.Type.CREATE, bgtGrpKey, bgtGrp);
        var createBgtgrpEventDuplicate = new Event(Event.Type.CREATE, bgtGrpKey, bgtGrp);
        saveInstrumentTreeProcessor.accept(createBgtgrpEvent);

        var instrumentGraph = instrumentGraphRepository.findAll().collectList().block();
        assertEquals(6, instrumentGraph.size());

        saveInstrumentTreeProcessor.accept(createTenantEventDuplicate);
        instrumentGraph = instrumentGraphRepository.findAll().collectList().block();
        assertEquals(6, instrumentGraph.size());
        saveInstrumentTreeProcessor.accept(createBudgetPfEventDuplicate);
        instrumentGraph = instrumentGraphRepository.findAll().collectList().block();
        assertEquals(6, instrumentGraph.size());
        saveInstrumentTreeProcessor.accept(createBgtgrpEventDuplicate);
        instrumentGraph = instrumentGraphRepository.findAll().collectList().block();
        assertEquals(6, instrumentGraph.size());
    }

    private boolean existsInGraph(List<InstrumentGraphEntry> entries, String businesskey, String parent, int pathlength){
        final boolean[] containsEntry = {false};
        entries.forEach(i->{
            if(i.getAncestor().equals(parent) && i.getDescendant().equals(businesskey) && i.getPathlength()==pathlength && i.getEdgetype().equals(EdgeType.TENANTGRAPH)) {
                containsEntry[0] =true;
            }
        });
        return containsEntry[0];
    }
}
