package de.hf.myfinance.instruments;

import de.hf.myfinance.event.Event;
import de.hf.myfinance.instruments.persistence.DataReaderImpl;
import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.service.InstrumentService;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@Import({TestChannelBinderConfiguration.class})
public class DataReaderTests extends EventProcessorTestBase {

    String tenantKey = "aTest@6";
    String tenantDesc = "aTest";
    String budgetPfdesc = "bgtPf_" + tenantDesc;
    String budgetPfKey = budgetPfdesc + "@23";
    String bgtGrpdesc = "bgtGrp_" + budgetPfdesc;
    String bgtGrpKey = bgtGrpdesc + "@10";
    String bgtdesc = "incomeBgt_" + bgtGrpdesc;
    String bgtKey = bgtdesc + "@10";
    String accPfdesc = "accPf_" + tenantDesc;
    String accPfKey = accPfdesc + "@8";
    String giroDesc = "newGiro";
    String giroKey = "newGiro@1";
    String currencyDesc = "newCurrency";
    String currencyCode = "USD";

    @Autowired
    DataReaderImpl dataReader;
    @Autowired
    InstrumentRepository instrumentRepository;
    @Autowired
    InstrumentGraphRepository instrumentGraphRepository;

    @Autowired
    private OutputDestination target;

    @Autowired
    @Qualifier("saveInstrumentProcessor")
    protected Consumer<Event<String, Instrument>> saveInstrumentProcessor;

    @Autowired
    @Qualifier("saveInstrumentTreeProcessor")
    protected Consumer<Event<String, Instrument>> saveInstrumentTreeProcessor;

    @BeforeEach
    void setupDb() {
        instrumentRepository.deleteAll().block();
        instrumentGraphRepository.deleteAll().block();
        purgeMessages("instrumentApproved-out-0");
    }

    @Test
    void getRootTest() {
        setupTestTenant();

        var result = dataReader.getRootInstrument(bgtKey, EdgeType.TENANTGRAPH).block();
        assertEquals(tenantKey, result);

    }

    private void setupTestTenant() {
        var newInstrument = new Instrument(tenantDesc, InstrumentType.TENANT);
        newInstrument.setBusinesskey(tenantKey);
        Event creatEvent = new Event(Event.Type.CREATE, tenantKey, newInstrument);
        saveInstrumentProcessor.accept(creatEvent);
        saveInstrumentTreeProcessor.accept(creatEvent);


        var budgetPf = new Instrument(budgetPfdesc, InstrumentType.BUDGETPORTFOLIO);
        budgetPf.setBusinesskey(budgetPfKey);
        budgetPf.setParentBusinesskey(tenantKey);
        creatEvent = new Event(Event.Type.CREATE, budgetPfKey, budgetPf);
        saveInstrumentProcessor.accept(creatEvent);
        saveInstrumentTreeProcessor.accept(creatEvent);


        var bgtGrp = new Instrument(bgtGrpdesc, InstrumentType.BUDGETGROUP);
        bgtGrp.setBusinesskey(bgtGrpKey);
        bgtGrp.setParentBusinesskey(budgetPfKey);
        creatEvent = new Event(Event.Type.CREATE, bgtGrpKey, bgtGrp);
        saveInstrumentProcessor.accept(creatEvent);
        saveInstrumentTreeProcessor.accept(creatEvent);

        var bgt = new Instrument(bgtGrpdesc, InstrumentType.BUDGET);
        bgt.setBusinesskey(bgtKey);
        bgt.setParentBusinesskey(bgtGrpKey);
        creatEvent = new Event(Event.Type.CREATE, bgtKey, bgt);
        saveInstrumentProcessor.accept(creatEvent);
        saveInstrumentTreeProcessor.accept(creatEvent);

        var accPf = new Instrument(accPfdesc, InstrumentType.ACCOUNTPORTFOLIO);
        accPf.setBusinesskey(accPfKey);
        accPf.setParentBusinesskey(tenantKey);
        creatEvent = new Event(Event.Type.CREATE, accPfKey, accPf);
        saveInstrumentProcessor.accept(creatEvent);
        saveInstrumentTreeProcessor.accept(creatEvent);
    }

}