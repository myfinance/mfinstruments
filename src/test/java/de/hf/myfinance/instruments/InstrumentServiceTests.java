package de.hf.myfinance.instruments;

import de.hf.myfinance.event.Event;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.service.InstrumentService;
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


import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@Import({TestChannelBinderConfiguration.class})
class InstrumentServiceTests extends EventProcessorTestBase {



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
    protected Consumer<Event<Integer, Instrument>> saveInstrumentProcessor;

    @Autowired
    @Qualifier("saveInstrumentTreeProcessor")
    protected Consumer<Event<Integer, Instrument>> saveInstrumentTreeProcessor;

    @BeforeEach
    void setupDb() {
        instrumentRepository.deleteAll().block();
        instrumentGraphRepository.deleteAll().block();
        purgeMessages("instrumentapproved-out-0");
    }

    @Test
    void contextLoads() {
    }

    @Test
    void createTenant() {
        var tenantKey = "aTest@6";
        var tenantDesc = "aTest";
        var newTenant = new Instrument(tenantDesc, InstrumentType.TENANT);
        instrumentService.addInstrument(newTenant).block();
        //StepVerifier.create(instrumentService.listInstruments()).expectNextCount(5).verifyComplete();
        final List<String> messages = getMessages("instrumentapproved-out-0");
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

        data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(1))).get("data");
        assertEquals("bgtPf_aTest@23", data.get("businesskey"));
        assertEquals("bgtPf_aTest", data.get("description"));
        assertEquals(true, data.get("isactive"));
        assertEquals("BUDGETPORTFOLIO", data.get("instrumentType"));
        assertEquals("aTest@6", data.get("parentBusinesskey"));

        data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(2))).get("data");
        assertEquals("bgtGrp_bgtPf_aTest@10", data.get("businesskey"));
        assertEquals("bgtGrp_bgtPf_aTest", data.get("description"));
        assertEquals(true, data.get("isactive"));
        assertEquals("BUDGETGROUP", data.get("instrumentType"));
        assertEquals("bgtPf_aTest@23", data.get("parentBusinesskey"));

        data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(3))).get("data");
        assertEquals("incomeBgt_bgtGrp_bgtPf_aTest@5", data.get("businesskey"));
        assertEquals("incomeBgt_bgtGrp_bgtPf_aTest", data.get("description"));
        assertEquals(true, data.get("isactive"));
        assertEquals("BUDGET", data.get("instrumentType"));
        assertEquals("bgtGrp_bgtPf_aTest@10", data.get("parentBusinesskey"));

        data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(4))).get("data");
        assertEquals("accPf_aTest@8", data.get("businesskey"));
        assertEquals("accPf_aTest", data.get("description"));
        assertEquals(true, data.get("isactive"));
        assertEquals("ACCOUNTPORTFOLIO", data.get("instrumentType"));
        assertEquals("aTest@6", data.get("parentBusinesskey"));
    }

    @Test
    void getInstruments() {
        var tenantKey = "aTest@6";
        var tenantDesc = "aTest";
        var newInstrument = new Instrument(tenantDesc, InstrumentType.TENANT);
        newInstrument.setBusinesskey(tenantKey);
        Event creatEvent = new Event(Event.Type.CREATE, tenantKey, newInstrument);
        saveInstrumentProcessor.accept(creatEvent);
        saveInstrumentTreeProcessor.accept(creatEvent);

        var instruments = instrumentRepository.findAll().collectList().block();
        assertEquals(1, instruments.size());

        var instrumentGraph = instrumentGraphRepository.findAll().collectList().block();
        assertEquals(1, instrumentGraph.size());

        var budgetPfdesc = "bgtPf_"+tenantDesc;
        var budgetPfKey = budgetPfdesc+"@23";
        var budgetPf = new Instrument(budgetPfdesc, InstrumentType.BUDGETPORTFOLIO);
        budgetPf.setBusinesskey(budgetPfKey);
        budgetPf.setParentBusinesskey(tenantKey);
        creatEvent = new Event(Event.Type.CREATE, budgetPfKey, budgetPf);
        saveInstrumentProcessor.accept(creatEvent);
        saveInstrumentTreeProcessor.accept(creatEvent);

        var bgtGrpdesc = "bgtGrp_"+budgetPfdesc;
        var bgtGrpKey = bgtGrpdesc+"@10";
        var bgtGrp = new Instrument(bgtGrpdesc, InstrumentType.BUDGETGROUP);
        bgtGrp.setBusinesskey(bgtGrpKey);
        bgtGrp.setParentBusinesskey(budgetPfKey);
        creatEvent = new Event(Event.Type.CREATE, bgtGrpKey, bgtGrp);
        saveInstrumentProcessor.accept(creatEvent);
        saveInstrumentTreeProcessor.accept(creatEvent);

        var bgtdesc = "incomeBgt_"+bgtGrpdesc;
        var bgtKey = bgtdesc+"@10";
        var bgt = new Instrument(bgtGrpdesc, InstrumentType.BUDGET);
        bgt.setBusinesskey(bgtKey);
        bgt.setParentBusinesskey(bgtGrpKey);
        creatEvent = new Event(Event.Type.CREATE, bgtKey, bgt);
        saveInstrumentProcessor.accept(creatEvent);
        saveInstrumentTreeProcessor.accept(creatEvent);

        var accPfdesc = "accPf_"+tenantDesc;
        var accPfKey = accPfdesc+"@8";
        var accPf = new Instrument(accPfdesc, InstrumentType.ACCOUNTPORTFOLIO);
        accPf.setBusinesskey(accPfKey);
        accPf.setParentBusinesskey(tenantKey);
        creatEvent = new Event(Event.Type.CREATE, accPfKey, accPf);
        saveInstrumentProcessor.accept(creatEvent);
        saveInstrumentTreeProcessor.accept(creatEvent);

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

    /*@Test
    void createDuplicate() {

        var tenantKey = "aTest@6";
        var tenantDesc = "aTest";
        var newTenant = new Instrument(tenantDesc, InstrumentType.TENANT);
        instrumentService.addInstrument(newTenant).block();
        List<String> messages = getMessages("instrumentapproved-out-0");
        assertEquals(5, messages.size());

        purgeMessages("instrumentapproved-out-0");
        var duplicateTenant = new Instrument(tenantDesc, InstrumentType.TENANT);
        instrumentService.addInstrument(duplicateTenant).block();
        messages = getMessages("instrumentapproved-out-0");
        assertEquals(0, messages.size());

        var tenants = instrumentService.listTenants().collectList().block();
        assertEquals(1, tenants.size());
        var tenant = tenants.get(0);
        assertEquals(tenantKey, tenant.getBusinesskey());
        assertEquals(tenantDesc, tenant.getDescription());
        assertTrue(tenant.isIsactive());



        instrumentService.addInstrument(newTenant);
        instrumentService.addInstrument(newTenant).block();
        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(5).verifyComplete();

        tenants = instrumentService.listTenants().collectList().block();
        assertEquals(1, tenants.size());
        tenant = tenants.get(0);
        assertEquals(tenantKey, tenant.getBusinesskey());
        assertEquals(tenantDesc, tenant.getDescription());
        assertTrue(tenant.isIsactive());
    }*/
/*
    @Test
    void createInstrumentHandlerWithInvalidBusinesskey() {
        assertThrows(MFException.class, () -> {
            instrumentService.getInstrument("bla");
        });
    }

    @Test
    void createGiro() {
        var tenantKey = "aTest@6";
        var tenantDesc = "aTest";
        var newTenant = new Instrument(tenantDesc, InstrumentType.TENANT);
        instrumentService.addInstrument(newTenant).block();
        //StepVerifier.create(instrumentService.listInstruments()).expectNextCount(5).verifyComplete();
        final List<String> productMessages = getMessages("products");
        assertEquals(5, productMessages.size());

        var tenants = instrumentService.listTenants().collectList().block();
        assertEquals(1, tenants.size());
        var tenant = tenants.get(0);
        assertEquals(tenantKey, tenant.getBusinesskey());
        assertEquals(tenantDesc, tenant.getDescription());
        assertTrue(tenant.isIsactive());

        var accPfs = instrumentService.listInstrumentsByType(tenantKey, InstrumentType.ACCOUNTPORTFOLIO).collectList().block();
        assertEquals(1, accPfs.size());
        var accPf = accPfs.get(0);
        assertEquals("accPf_aTest@8", accPf.getBusinesskey());
        assertEquals("accPf_aTest", accPf.getDescription());
        assertTrue(accPf.isIsactive());


        var giroDesc = "newGiro";
        var giroKey = "newGiro@1";
        var newGiro = new Instrument(giroDesc, InstrumentType.GIRO);
        newGiro.setParentBusinesskey(accPf.getBusinesskey());
        var savedGiro = instrumentService.addInstrument(newGiro).block();
        assertEquals(giroDesc, savedGiro.getDescription());
        assertEquals(giroKey, savedGiro.getBusinesskey());
        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(6).verifyComplete();

        StepVerifier.create(instrumentService.listInstruments(tenantKey)).expectNextCount(5).verifyComplete();
    }

    @Test
    void createCurrency() {
        var desc = "newCurrency";
        var currencyCode = "USD";
        var currency = new Instrument(desc, InstrumentType.CURRENCY);
        currency.setBusinesskey(currencyCode);
        var savedCurrency = instrumentService.addInstrument(currency).block();
        assertEquals(desc, savedCurrency.getDescription());
        assertEquals(currencyCode, savedCurrency.getBusinesskey());
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
        assertEquals(desc, savedEq.getDescription());
        assertEquals(isin, savedEq.getBusinesskey());
        assertEquals(1, savedEq.getAdditionalMaps().get(AdditionalMaps.EQUITYSYMBOLS).size());
        assertEquals("USD", savedEq.getAdditionalMaps().get(AdditionalMaps.EQUITYSYMBOLS).get("MYSYMBOL"));
        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(1).verifyComplete();
    }*/
}
