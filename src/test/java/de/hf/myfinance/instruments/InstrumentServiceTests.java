package de.hf.myfinance.instruments;

import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.event.Event;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.service.InstrumentService;
import de.hf.myfinance.restmodel.AdditionalMaps;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import de.hf.testhelper.JsonHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@Import({TestChannelBinderConfiguration.class})
class InstrumentServiceTests extends EventProcessorTestBase {

    String tenantKey = "aTest@6";
    String tenantDesc = "aTest";
    String budgetPfdesc = "bgtPf_"+tenantDesc;
    String budgetPfKey = budgetPfdesc+"@23";
    String bgtGrpdesc = "bgtGrp_"+budgetPfdesc;
    String bgtGrpKey = bgtGrpdesc+"@10";
    String bgtdesc = "incomeBgt_"+bgtGrpdesc;
    String bgtKey = bgtdesc+"@10";
    String accPfdesc = "accPf_"+tenantDesc;
    String accPfKey = accPfdesc+"@8";
    String giroDesc = "newGiro";
    String giroKey = "newGiro@1";
    String currencyDesc = "newCurrency";
    String currencyCode = "USD";

    @Autowired
    InstrumentService instrumentService;
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
    void contextLoads() {
    }

    @Test
    void createTenant() {
        var newTenant = new Instrument(tenantDesc, InstrumentType.TENANT);
        instrumentService.addInstrument(newTenant).block();
        final List<String> messages = getMessages("instrumentApproved-out-0");
        assertEquals(5, messages.size());
        LOG.info(messages.get(0));
        Event createTenantEvent = new Event(Event.Type.CREATE, tenantKey, newTenant);
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(tenantKey, data.get("businesskey"));
        assertEquals(tenantDesc, data.get("description"));
        assertEquals(true, data.get("isactive"));
        assertEquals("TENANT", data.get("instrumentType"));
        assertTrue(data.get("parentBusinesskey")==null);
        assertTrue(data.get("tenantBusinesskey")==null);

        data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(1))).get("data");
        assertEquals("bgtPf_aTest@23", data.get("businesskey"));
        assertEquals("bgtPf_aTest", data.get("description"));
        assertEquals(true, data.get("isactive"));
        assertEquals("BUDGETPORTFOLIO", data.get("instrumentType"));
        assertEquals("aTest@6", data.get("parentBusinesskey"));
        assertEquals("aTest@6", data.get("tenantBusinesskey"));

        data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(2))).get("data");
        assertEquals("bgtGrp_bgtPf_aTest@10", data.get("businesskey"));
        assertEquals("bgtGrp_bgtPf_aTest", data.get("description"));
        assertEquals(true, data.get("isactive"));
        assertEquals("BUDGETGROUP", data.get("instrumentType"));
        assertEquals("bgtPf_aTest@23", data.get("parentBusinesskey"));
        assertEquals("aTest@6", data.get("tenantBusinesskey"));

        data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(3))).get("data");
        assertEquals("incomeBgt_bgtGrp_bgtPf_aTest@5", data.get("businesskey"));
        assertEquals("incomeBgt_bgtGrp_bgtPf_aTest", data.get("description"));
        assertEquals(true, data.get("isactive"));
        assertEquals("BUDGET", data.get("instrumentType"));
        assertEquals("bgtGrp_bgtPf_aTest@10", data.get("parentBusinesskey"));
        assertEquals("aTest@6", data.get("tenantBusinesskey"));

        data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(4))).get("data");
        assertEquals("accPf_aTest@8", data.get("businesskey"));
        assertEquals("accPf_aTest", data.get("description"));
        assertEquals(true, data.get("isactive"));
        assertEquals("ACCOUNTPORTFOLIO", data.get("instrumentType"));
        assertEquals("aTest@6", data.get("parentBusinesskey"));
        assertEquals("aTest@6", data.get("tenantBusinesskey"));
    }

    @Test
    void getInstruments() {
        setupTestTenant();

        var instruments = instrumentRepository.findAll().collectList().block();
        assertEquals(5, instruments.size());

        var instrumentGraph = instrumentGraphRepository.findAll().collectList().block();
        assertEquals(12, instrumentGraph.size());


        var tenants = instrumentService.listTenants().collectList().block();
        assertEquals(1, tenants.size());
        var tenant = tenants.get(0);
        assertEquals(tenantKey, tenant.getBusinesskey());
        assertEquals(tenantDesc, tenant.getDescription());
        assertTrue(tenant.isIsactive());

        var accPfs = instrumentService.listInstrumentsByType(tenantKey, InstrumentType.ACCOUNTPORTFOLIO).collectList().block();
        assertEquals(1, accPfs.size());
        var savedAcPf = accPfs.get(0);
        assertEquals(accPfKey, savedAcPf.getBusinesskey());
        assertEquals(accPfdesc, savedAcPf.getDescription());
        assertTrue(savedAcPf.isIsactive());

        StepVerifier.create(instrumentService.listInstruments(tenantKey)).expectNextCount(4).verifyComplete();
    }


    @Test
    void createInstrumentHandlerWithInvalidBusinesskey() {
        assertThrows(MFException.class, () -> {
            instrumentService.getInstrument("bla");
        });
    }

    @Test
    void createGiro() {

        setupTestTenant();

        var tenants = instrumentService.listTenants().collectList().block();
        assertEquals(1, tenants.size());
        var tenant = tenants.get(0);
        assertEquals(tenantKey, tenant.getBusinesskey());
        assertEquals(tenantDesc, tenant.getDescription());
        assertTrue(tenant.isIsactive());

        var accPfs = instrumentService.listInstrumentsByType(tenantKey, InstrumentType.ACCOUNTPORTFOLIO).collectList().block();
        assertEquals(1, accPfs.size());
        var accPf = accPfs.get(0);
        assertEquals(accPfKey, accPf.getBusinesskey());
        assertEquals(accPfdesc, accPf.getDescription());
        assertTrue(accPf.isIsactive());


        var newGiro = new Instrument(giroDesc, InstrumentType.GIRO);
        newGiro.setParentBusinesskey(accPf.getBusinesskey());
        instrumentService.addInstrument(newGiro).block();
        final List<String> messages = getMessages("instrumentApproved-out-0");
        assertEquals(1, messages.size());
        LOG.info(messages.get(0));
        Event createEvent = new Event(Event.Type.CREATE, giroKey, newGiro);
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(giroKey, data.get("businesskey"));
        assertEquals(giroDesc, data.get("description"));
        assertEquals(true, data.get("isactive"));
        assertEquals("GIRO", data.get("instrumentType"));
        assertEquals(accPfKey, data.get("parentBusinesskey"));
        assertEquals("aTest@6", data.get("tenantBusinesskey"));

        saveInstrumentProcessor.accept(createEvent);
        saveInstrumentTreeProcessor.accept(createEvent);


        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(6).verifyComplete();

        StepVerifier.create(instrumentService.listInstruments(tenantKey)).expectNextCount(5).verifyComplete();
    }

    @Test
    void createCurrency() {

        var currency = new Instrument(currencyDesc, InstrumentType.CURRENCY);
        currency.setBusinesskey(currencyCode);
        instrumentService.addInstrument(currency).block();
        final List<String> messages = getMessages("instrumentApproved-out-0");
        assertEquals(1, messages.size());
        LOG.info(messages.get(0));
        Event createEvent = new Event(Event.Type.CREATE, currencyCode, currency);
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(currencyCode, data.get("businesskey"));
        assertEquals(currencyDesc, data.get("description"));
        assertEquals(true, data.get("isactive"));
        assertEquals("CURRENCY", data.get("instrumentType"));
        assertNull(data.get("parentBusinesskey"));
        assertNull(data.get("tenantBusinesskey"));
        saveInstrumentProcessor.accept(createEvent);
        saveInstrumentTreeProcessor.accept(createEvent);
        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(1).verifyComplete();
    }

    @Test
    void createEquity() {
        var desc = "newEquity";
        var isin = "de000001";
        var symbols = new HashMap<String, String>();
        symbols.put("MYSYMBOL", "USD");
        Map<AdditionalMaps, Map<String, String>> additionalMaps = new HashMap<>();
        additionalMaps.put(AdditionalMaps.EQUITYSYMBOLS, symbols);
        var eq = new Instrument(desc, InstrumentType.EQUITY);
        eq.setAdditionalMaps(additionalMaps);
        eq.setBusinesskey(isin);
        var savedEq = instrumentService.addInstrument(eq).block();
        final List<String> messages = getMessages("instrumentApproved-out-0");
        assertEquals(1, messages.size());
        LOG.info(messages.get(0));
        Event createEvent = new Event(Event.Type.CREATE, isin, eq);
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(isin, data.get("businesskey"));
        assertEquals(desc, data.get("description"));
        assertEquals(true, data.get("isactive"));
        assertEquals("EQUITY", data.get("instrumentType"));
        assertNull(data.get("parentBusinesskey"));
        assertNull(data.get("tenantBusinesskey"));
        var maps = (HashMap)data.get("additionalMaps");
        assertEquals(1, maps.size());
        assertEquals("USD", ((HashMap)maps.get("EQUITYSYMBOLS")).get("MYSYMBOL"));
        saveInstrumentProcessor.accept(createEvent);
        saveInstrumentTreeProcessor.accept(createEvent);
        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(1).verifyComplete();
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
