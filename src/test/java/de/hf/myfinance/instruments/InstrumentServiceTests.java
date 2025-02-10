package de.hf.myfinance.instruments;

import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.event.Event;
import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.service.InstrumentService;
import de.hf.myfinance.restmodel.*;
import de.hf.testhelper.JsonHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;


import java.time.LocalDate;
import java.util.*;
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
    String depotDesc = "newDepot";
    String depotKey = "newDepot@11";
    String realestateDesc = "newRealestate";
    String realestateKey = realestateDesc+"@21";
    String deprecationObjectDesc = "newDeprecationObject";
    String deprecationObjectKey = deprecationObjectDesc+"@20";
    String lifeInsurenceDesc = "newlifeInsurence";
    String lifeInsurenceKey = lifeInsurenceDesc+"@19";
    String loanDesc = "newLoan";
    String loanKey = loanDesc+"@22";
    String moneyAtCallDesc = "newMoneyAtCall";
    String moneyAtCallKey = moneyAtCallDesc+"@2";
    String timeDepositDesc = "newTimeDeposit";
    String timeDepositKey = timeDepositDesc+"@3";
    String buildingsavingAccountDesc = "newBuildingsavingAccount";
    String buildingsavingAccountKey = buildingsavingAccountDesc+"@4";
    String etfDesc = "newEtf";
    String etfKey = etfDesc+"@16";
    String fondDesc = "newFond";
    String fondKey = fondDesc+"@15";
    String bondDesc = "newBond";
    String bondKey = bondDesc+"@18";

    @Autowired
    InstrumentService instrumentService;

    @Autowired
    private OutputDestination target;

    @Autowired
    @Qualifier("saveInstrumentProcessor")
    protected Consumer<Event<String, Instrument>> saveInstrumentProcessor;

    @Autowired
    @Qualifier("saveInstrumentTreeProcessor")
    protected Consumer<Event<String, Instrument>> saveInstrumentTreeProcessor;

    @Autowired
    @Qualifier("valueProcessor")
    protected Consumer<Event<String, ValueCurve>> valueProcessor;

    @Test
    void createTenant() {
        var newTenant = new Instrument(tenantDesc, InstrumentType.TENANT);
        instrumentService.saveInstrument(newTenant).block();
        final List<String> messages = getMessages("instrumentApproved-out-0");
        assertEquals(5, messages.size());
        LOG.info(messages.get(0));
        Event createTenantEvent = new Event(Event.Type.CREATE, tenantKey, newTenant);
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(tenantKey, data.get("businesskey"));
        assertEquals(tenantDesc, data.get("description"));
        assertEquals(true, data.get("active"));
        assertEquals("TENANT", data.get("instrumentType"));
        assertNull(data.get("parentBusinesskey"));
        assertNull(data.get("tenantBusinesskey"));

        data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(1))).get("data");
        assertEquals("bgtPf_aTest@23", data.get("businesskey"));
        assertEquals("bgtPf_aTest", data.get("description"));
        assertEquals(true, data.get("active"));
        assertEquals("BUDGETPORTFOLIO", data.get("instrumentType"));
        assertEquals("aTest@6", data.get("parentBusinesskey"));
        assertEquals("aTest@6", data.get("tenantBusinesskey"));

        data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(2))).get("data");
        assertEquals("bgtGrp_bgtPf_aTest@10", data.get("businesskey"));
        assertEquals("bgtGrp_bgtPf_aTest", data.get("description"));
        assertEquals(true, data.get("active"));
        assertEquals("BUDGETGROUP", data.get("instrumentType"));
        assertEquals("bgtPf_aTest@23", data.get("parentBusinesskey"));
        assertEquals("aTest@6", data.get("tenantBusinesskey"));

        data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(3))).get("data");
        assertEquals("incomeBgt_bgtGrp_bgtPf_aTest@5", data.get("businesskey"));
        assertEquals("incomeBgt_bgtGrp_bgtPf_aTest", data.get("description"));
        assertEquals(true, data.get("active"));
        assertEquals("BUDGET", data.get("instrumentType"));
        assertEquals("bgtGrp_bgtPf_aTest@10", data.get("parentBusinesskey"));
        assertEquals("aTest@6", data.get("tenantBusinesskey"));

        data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(4))).get("data");
        assertEquals("accPf_aTest@8", data.get("businesskey"));
        assertEquals("accPf_aTest", data.get("description"));
        assertEquals(true, data.get("active"));
        assertEquals("ACCOUNTPORTFOLIO", data.get("instrumentType"));
        assertEquals("aTest@6", data.get("parentBusinesskey"));
        assertEquals("aTest@6", data.get("tenantBusinesskey"));
    }

    @Test
    void updateTenantFailedDueToMissingInstrument() {

        var newTenant = new Instrument(tenantDesc, InstrumentType.TENANT);   
        newTenant.setBusinesskey("bla");  

        assertThrows(MFException.class, () -> {
            instrumentService.saveInstrument(newTenant).block();   
        });
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
        assertTrue(tenant.isActive());

        var accPfs = instrumentService.listInstrumentsByType(tenantKey, InstrumentType.ACCOUNTPORTFOLIO).collectList().block();
        assertEquals(1, accPfs.size());
        var savedAcPf = accPfs.get(0);
        assertEquals(accPfKey, savedAcPf.getBusinesskey());
        assertEquals(accPfdesc, savedAcPf.getDescription());
        assertTrue(savedAcPf.isActive());

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
        assertTrue(tenant.isActive());

        var accPfs = instrumentService.listInstrumentsByType(tenantKey, InstrumentType.ACCOUNTPORTFOLIO).collectList().block();
        assertEquals(1, accPfs.size());
        var accPf = accPfs.get(0);
        assertEquals(accPfKey, accPf.getBusinesskey());
        assertEquals(accPfdesc, accPf.getDescription());
        assertTrue(accPf.isActive());


        var newGiro = new Instrument(giroDesc, InstrumentType.GIRO);
        newGiro.setParentBusinesskey(accPf.getBusinesskey());
        instrumentService.saveInstrument(newGiro).block();
        final List<String> messages = getMessages("instrumentApproved-out-0");
        assertEquals(1, messages.size());
        LOG.info(messages.get(0));
        Event createEvent = new Event(Event.Type.CREATE, giroKey, newGiro);
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(giroKey, data.get("businesskey"));
        assertEquals(giroDesc, data.get("description"));
        assertEquals(true, data.get("active"));
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
        var properties = new HashMap<AdditionalProperties, String>();
        properties.put(AdditionalProperties.CURRENCYCODE, currencyCode);
        currency.setAdditionalProperties(properties);
        instrumentService.saveInstrument(currency).block();
        final List<String> messages = getMessages("instrumentApproved-out-0");
        assertEquals(1, messages.size());
        LOG.info(messages.get(0));
        Event createEvent = new Event(Event.Type.CREATE, currencyCode, currency);
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(currencyCode+"@13", data.get("businesskey"));
        assertEquals(currencyDesc, data.get("description"));
        assertEquals(true, data.get("active"));
        assertEquals("CURRENCY", data.get("instrumentType"));
        assertNull(data.get("parentBusinesskey"));
        assertNull(data.get("tenantBusinesskey"));
        var propertiesMap = (HashMap)data.get("additionalProperties");
        assertEquals(1, propertiesMap.size());
        assertEquals("USD", (String)propertiesMap.get("CURRENCYCODE"));
        saveInstrumentProcessor.accept(createEvent);
        saveInstrumentTreeProcessor.accept(createEvent);
        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(1).verifyComplete();
    }

    @Test
    void createCurrencyFailedDueToMissingCurrencyCode() {

        var currency = new Instrument(currencyDesc, InstrumentType.CURRENCY);

        assertThrows(MFException.class, () -> {
            instrumentService.saveInstrument(currency).block();
        });
    }

    @Test
    void createEquity() {

        var currency = new Instrument(currencyDesc, InstrumentType.CURRENCY);
        var properties = new HashMap<AdditionalProperties, String>();
        properties.put(AdditionalProperties.CURRENCYCODE, currencyCode);
        currency.setAdditionalProperties(properties);
        currency.setBusinesskey("USD"+"@13");
        Event creatEvent = new Event(Event.Type.CREATE, "USD"+"@13", currency);
        saveInstrumentProcessor.accept(creatEvent);

        var desc = "newEquity";
        var isin = "de0000000001";
        var symbols = new HashMap<String, String>();
        symbols.put("MYSYMBOL", "USD"+"@13");
        Map<AdditionalMaps, Map<String, String>> additionalMaps = new HashMap<>();
        additionalMaps.put(AdditionalMaps.EQUITYSYMBOLS, symbols);
        var eq = new Instrument(desc, InstrumentType.EQUITY);
        eq.setAdditionalMaps(additionalMaps);
        var eqProperties = new HashMap<AdditionalProperties, String>();
        eqProperties.put(AdditionalProperties.ISIN, isin);
        eq.setAdditionalProperties(eqProperties);

        var savedEq = instrumentService.saveInstrument(eq).block();
        var messages = getMessages("instrumentApproved-out-0");
        assertEquals(1, messages.size());
        LOG.info(messages.get(0));
        Event createEvent = new Event(Event.Type.CREATE, isin, eq);
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(isin.toUpperCase()+"@14", data.get("businesskey"));
        assertEquals(desc, data.get("description"));
        assertEquals(true, data.get("active"));
        assertEquals("EQUITY", data.get("instrumentType"));
        assertNull(data.get("parentBusinesskey"));
        assertNull(data.get("tenantBusinesskey"));
        var maps = (HashMap)data.get("additionalMaps");
        assertEquals(1, maps.size());
        assertEquals("USD"+"@13", ((HashMap)maps.get("EQUITYSYMBOLS")).get("MYSYMBOL"));

        var propertiesMap = (HashMap)data.get("additionalProperties");
        assertEquals(1, propertiesMap.size());
        assertEquals(isin.toUpperCase(), (String)propertiesMap.get("ISIN"));

        saveInstrumentProcessor.accept(createEvent);
        saveInstrumentTreeProcessor.accept(createEvent);
        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(2).verifyComplete();
    }

    @Test
    void createEquityFailedDueToMissingCurrency() {
        var desc = "newEquity";
        var isin = "de0000000001";
        var symbols = new HashMap<String, String>();
        symbols.put("MYSYMBOL", "USD");
        Map<AdditionalMaps, Map<String, String>> additionalMaps = new HashMap<>();
        additionalMaps.put(AdditionalMaps.EQUITYSYMBOLS, symbols);
        var eq = new Instrument(desc, InstrumentType.EQUITY);
        eq.setAdditionalMaps(additionalMaps);
        var eqProperties = new HashMap<AdditionalProperties, String>();
        eqProperties.put(AdditionalProperties.ISIN, isin);
        eq.setAdditionalProperties(eqProperties);

        assertThrows(MFException.class, () -> {
            instrumentService.saveInstrument(eq).block();
        });
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
        budgetPf.setTenantBusinesskey(tenantKey);
        creatEvent = new Event(Event.Type.CREATE, budgetPfKey, budgetPf);
        saveInstrumentProcessor.accept(creatEvent);
        saveInstrumentTreeProcessor.accept(creatEvent);


        var bgtGrp = new Instrument(bgtGrpdesc, InstrumentType.BUDGETGROUP);
        bgtGrp.setBusinesskey(bgtGrpKey);
        bgtGrp.setParentBusinesskey(budgetPfKey);
        bgtGrp.setTenantBusinesskey(tenantKey);
        creatEvent = new Event(Event.Type.CREATE, bgtGrpKey, bgtGrp);
        saveInstrumentProcessor.accept(creatEvent);
        saveInstrumentTreeProcessor.accept(creatEvent);

        var bgt = new Instrument(bgtGrpdesc, InstrumentType.BUDGET);
        bgt.setBusinesskey(bgtKey);
        bgt.setParentBusinesskey(bgtGrpKey);
        bgt.setTenantBusinesskey(tenantKey);
        creatEvent = new Event(Event.Type.CREATE, bgtKey, bgt);
        saveInstrumentProcessor.accept(creatEvent);
        saveInstrumentTreeProcessor.accept(creatEvent);

        var accPf = new Instrument(accPfdesc, InstrumentType.ACCOUNTPORTFOLIO);
        accPf.setBusinesskey(accPfKey);
        accPf.setParentBusinesskey(tenantKey);
        accPf.setTenantBusinesskey(tenantKey);
        creatEvent = new Event(Event.Type.CREATE, accPfKey, accPf);
        saveInstrumentProcessor.accept(creatEvent);
        saveInstrumentTreeProcessor.accept(creatEvent);
    }


    @Test
    void inactivateGiro() {

        setupTestTenant();

        var newGiro = new Instrument(giroDesc, InstrumentType.GIRO);
        newGiro.setBusinesskey(giroKey);
        newGiro.setParentBusinesskey(accPfKey);

        Event creatEvent = new Event(Event.Type.CREATE, giroKey, newGiro);
        saveInstrumentProcessor.accept(creatEvent);
        saveInstrumentTreeProcessor.accept(creatEvent);

        var valueCurve = new ValueCurve();
        TreeMap<LocalDate, Double> values = new TreeMap<>();
        values.put(LocalDate.of(2022,1,1), 0.0);
        values.put(LocalDate.of(2022,1,2), 1000.0);
        values.put(LocalDate.of(2022,1,3), 0.0);
        valueCurve.setValueCurve(values);
        valueCurve.setInstrumentBusinesskey(giroKey);
        Event createValueEvent = new Event(Event.Type.CREATE, giroKey, valueCurve);
        valueProcessor.accept(createValueEvent);

        var savedInstrument = instrumentRepository.findByBusinesskey(giroKey).block();
        assertEquals(giroKey, savedInstrument.getBusinesskey());
        assertEquals(true, savedInstrument.isActive());

        newGiro.setActive(false);
        instrumentService.saveInstrument(newGiro).block();

        final List<String> messages = getMessages("instrumentApproved-out-0");
        assertEquals(1, messages.size());
        LOG.info(messages.get(0));
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(giroKey, data.get("businesskey"));
        assertEquals(giroDesc, data.get("description"));
        assertEquals(false, data.get("active"));
        assertEquals("GIRO", data.get("instrumentType"));
    }

    @Test
    void inactivateGiroNotAllowed() {

        setupTestTenant();

        var newGiro = new Instrument(giroDesc, InstrumentType.GIRO);
        newGiro.setBusinesskey(giroKey);
        newGiro.setParentBusinesskey(accPfKey);

        Event creatEvent = new Event(Event.Type.CREATE, giroKey, newGiro);
        saveInstrumentProcessor.accept(creatEvent);
        saveInstrumentTreeProcessor.accept(creatEvent);

        var valueCurve = new ValueCurve();
        TreeMap<LocalDate, Double> values = new TreeMap<>();
        values.put(LocalDate.of(2022,1,1), 0.0);
        values.put(LocalDate.of(2022,1,2), 1000.0);
        values.put(LocalDate.of(2022,1,3), 100.0);
        valueCurve.setValueCurve(values);
        valueCurve.setInstrumentBusinesskey(giroKey);
        Event createValueEvent = new Event(Event.Type.CREATE, giroKey, valueCurve);
        valueProcessor.accept(createValueEvent);

        var savedInstrument = instrumentRepository.findByBusinesskey(giroKey).block();
        assertEquals(giroKey, savedInstrument.getBusinesskey());
        assertEquals(true, savedInstrument.isActive());

        newGiro.setActive(false);

        assertThrows(MFException.class, () -> {
            instrumentService.saveInstrument(newGiro).block();
        });

    }

    @Test
    void inactivateEquityAllwaysAllowed() {
        setupTestTenant();
        var desc = "newEquity";
        var isin = "de0000000001";
        var newEq = new Instrument(desc, InstrumentType.EQUITY);
        newEq.setBusinesskey(isin);

        Event creatEvent = new Event(Event.Type.CREATE, isin, newEq);
        saveInstrumentProcessor.accept(creatEvent);
        saveInstrumentTreeProcessor.accept(creatEvent);

        var valueCurve = new ValueCurve();
        TreeMap<LocalDate, Double> values = new TreeMap<>();
        values.put(LocalDate.of(2022,1,1), 0.0);
        values.put(LocalDate.of(2022,1,2), 1000.0);
        values.put(LocalDate.of(2022,1,3), 100.0);
        valueCurve.setValueCurve(values);
        valueCurve.setInstrumentBusinesskey(isin);
        Event createValueEvent = new Event(Event.Type.CREATE, isin, valueCurve);
        valueProcessor.accept(createValueEvent);

        var savedInstrument = instrumentRepository.findByBusinesskey(isin).block();
        assertEquals(isin, savedInstrument.getBusinesskey());
        assertEquals(true, savedInstrument.isActive());

        newEq.setActive(false);

        instrumentService.saveInstrument(newEq).block();
        final List<String> messages = getMessages("instrumentApproved-out-0");
        assertEquals(1, messages.size());
        LOG.info(messages.get(0));
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(isin, data.get("businesskey"));
        assertEquals(desc, data.get("description"));
        assertEquals(false, data.get("active"));
        assertEquals("EQUITY", data.get("instrumentType"));

    }

    @Test
    void listAllAccounts() {

        setupTestTenant();
        var newGiro = new Instrument(giroDesc, InstrumentType.GIRO);
        newGiro.setParentBusinesskey(accPfKey);
        Event creatEvent = new Event(Event.Type.CREATE, giroKey, newGiro);
        saveInstrumentProcessor.accept(creatEvent);
        saveInstrumentTreeProcessor.accept(creatEvent);

        StepVerifier.create(instrumentService.listAccounts(tenantKey)).expectNextCount(1).verifyComplete();
        
    }


    @Test
    void listAllBudgets() {

        setupTestTenant();


        StepVerifier.create(instrumentService.listBudgets(tenantKey)).expectNextCount(1).verifyComplete();
        
    }

    @Test
    void createDepot() {

        setupTestTenant();

        var tenants = instrumentService.listTenants().collectList().block();
        assertEquals(1, tenants.size());
        var tenant = tenants.get(0);
        assertEquals(tenantKey, tenant.getBusinesskey());
        assertEquals(tenantDesc, tenant.getDescription());
        assertTrue(tenant.isActive());

        var accPfs = instrumentService.listInstrumentsByType(tenantKey, InstrumentType.ACCOUNTPORTFOLIO).collectList().block();
        assertEquals(1, accPfs.size());
        var accPf = accPfs.get(0);
        assertEquals(accPfKey, accPf.getBusinesskey());
        assertEquals(accPfdesc, accPf.getDescription());
        assertTrue(accPf.isActive());


        var newDepot = new Instrument(depotDesc, InstrumentType.DEPOT);
        newDepot.setParentBusinesskey(accPf.getBusinesskey());
        var propertyMap = new HashMap<AdditionalProperties, String>();
        propertyMap.put(AdditionalProperties.VALUEBUDGETID, bgtKey);
        newDepot.setAdditionalProperties(propertyMap);
        instrumentService.saveInstrument(newDepot).block();
        final List<String> messages = getMessages("instrumentApproved-out-0");
        assertEquals(1, messages.size());
        LOG.info(messages.get(0));
        Event<String, Instrument> createEvent = new Event<>(Event.Type.CREATE, depotKey, newDepot);
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(depotKey, data.get("businesskey"));
        assertEquals(depotDesc, data.get("description"));
        assertEquals(true, data.get("active"));
        assertEquals("DEPOT", data.get("instrumentType"));
        assertEquals(accPfKey, data.get("parentBusinesskey"));
        assertEquals("aTest@6", data.get("tenantBusinesskey"));

        var propertiesMap = (HashMap)data.get("additionalProperties");
        assertEquals(1, propertiesMap.size());
        assertEquals(bgtKey, (String)propertiesMap.get("VALUEBUDGETID"));

        saveInstrumentProcessor.accept(createEvent);
        saveInstrumentTreeProcessor.accept(createEvent);


        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(6).verifyComplete();

        StepVerifier.create(instrumentService.listInstruments(tenantKey)).expectNextCount(5).verifyComplete();
    }


    @Test
    void createNoDepotWithoutValueBudget() {

        setupTestTenant();

        var tenants = instrumentService.listTenants().collectList().block();
        assertEquals(1, tenants.size());
        var tenant = tenants.get(0);
        assertEquals(tenantKey, tenant.getBusinesskey());
        assertEquals(tenantDesc, tenant.getDescription());
        assertTrue(tenant.isActive());

        var accPfs = instrumentService.listInstrumentsByType(tenantKey, InstrumentType.ACCOUNTPORTFOLIO).collectList().block();
        assertEquals(1, accPfs.size());
        var accPf = accPfs.get(0);
        assertEquals(accPfKey, accPf.getBusinesskey());
        assertEquals(accPfdesc, accPf.getDescription());
        assertTrue(accPf.isActive());


        var newDepot = new Instrument(depotDesc, InstrumentType.DEPOT);
        newDepot.setParentBusinesskey(accPf.getBusinesskey());
        


        assertThrows(MFException.class, () -> {
            instrumentService.saveInstrument(newDepot).block();
        });
    }

    @Test
    void createNoDepotWithoutValidValueBudget() {

        setupTestTenant();

        var tenants = instrumentService.listTenants().collectList().block();
        assertEquals(1, tenants.size());
        var tenant = tenants.get(0);
        assertEquals(tenantKey, tenant.getBusinesskey());
        assertEquals(tenantDesc, tenant.getDescription());
        assertTrue(tenant.isActive());

        var accPfs = instrumentService.listInstrumentsByType(tenantKey, InstrumentType.ACCOUNTPORTFOLIO).collectList().block();
        assertEquals(1, accPfs.size());
        var accPf = accPfs.get(0);
        assertEquals(accPfKey, accPf.getBusinesskey());
        assertEquals(accPfdesc, accPf.getDescription());
        assertTrue(accPf.isActive());


        var newDepot = new Instrument(depotDesc, InstrumentType.DEPOT);
        newDepot.setParentBusinesskey(accPf.getBusinesskey());
        var propertyMap = new HashMap<AdditionalProperties, String>();
        propertyMap.put(AdditionalProperties.VALUEBUDGETID, "noexistingBudget");
        newDepot.setAdditionalProperties(propertyMap);


        assertThrows(MFException.class, () -> {
            instrumentService.saveInstrument(newDepot).block();
        });
    }

    @Test
    void createRealEstate() {
        setupTestTenant();
        var accPfs = instrumentService.listInstrumentsByType(tenantKey, InstrumentType.ACCOUNTPORTFOLIO).collectList().block();
        assertEquals(1, accPfs.size());
        var accPf = accPfs.get(0);
        assertEquals(accPfKey, accPf.getBusinesskey());
        assertEquals(accPfdesc, accPf.getDescription());
        assertTrue(accPf.isActive());

        var newRealEstate = new Instrument(realestateDesc, InstrumentType.REALESTATE);
        newRealEstate.setParentBusinesskey(accPf.getBusinesskey());

        var yieldgoaldate = "2025-02-10";
        var yieldgoalvalue = "2.5";
        var yieldgoals = new HashMap<String, String>();
        yieldgoals.put(yieldgoaldate, yieldgoalvalue);
        Map<AdditionalMaps, Map<String, String>> additionalMaps = new HashMap<>();
        additionalMaps.put(AdditionalMaps.YIELDGOAL, yieldgoals);
        var profitdate = "2025-01-10";
        var profitvalue = "1000";
        var profits = new HashMap<String, String>();
        profits.put(profitdate, profitvalue);
        additionalMaps.put(AdditionalMaps.REALESTATEPROFITS, profits);
        newRealEstate.setAdditionalMaps(additionalMaps);
        var properties = new HashMap<AdditionalProperties, String>();
        properties.put(AdditionalProperties.VALUEBUDGETID, bgtKey);
        newRealEstate.setAdditionalProperties(properties);

        instrumentService.saveInstrument(newRealEstate).block();
        final List<String> messages = getMessages("instrumentApproved-out-0");
        assertEquals(3, messages.size());
        LOG.info(messages.get(0));
        
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(realestateKey, data.get("businesskey"));
        assertEquals(realestateDesc, data.get("description"));
        assertEquals(true, data.get("active"));
        assertEquals("REALESTATE", data.get("instrumentType"));
        assertEquals(accPfKey, data.get("parentBusinesskey"));
        assertEquals(tenantKey, data.get("tenantBusinesskey"));
        var propertiesMap = (HashMap)data.get("additionalProperties");
        assertEquals(1, propertiesMap.size());
        assertEquals(bgtKey, (String)propertiesMap.get("VALUEBUDGETID"));
        var maps = (HashMap)data.get("additionalMaps");
        assertEquals(2, maps.size());
        assertEquals(yieldgoalvalue, ((HashMap)maps.get("YIELDGOAL")).get(yieldgoaldate));
        assertEquals(profitvalue, ((HashMap)maps.get("REALESTATEPROFITS")).get(profitdate));

        data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(1))).get("data");
        assertEquals("bgtGrp_"+realestateDesc+"@10", data.get("businesskey"));
        assertEquals("bgtGrp_"+realestateDesc, data.get("description"));
        assertEquals(true, data.get("active"));
        assertEquals("BUDGETGROUP", data.get("instrumentType"));
        assertEquals(budgetPfKey, data.get("parentBusinesskey"));
        assertEquals(tenantKey, data.get("tenantBusinesskey"));
        var budgetGroupPropertiesMap = (HashMap)data.get("additionalProperties");
        assertEquals(1, budgetGroupPropertiesMap.size());
        assertEquals(realestateKey, (String)budgetGroupPropertiesMap.get("LINKEDINSTRUMENTID"));

        data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(2))).get("data");
        assertEquals("incomeBgt_bgtGrp_newRealestate@5", data.get("businesskey"));
        assertEquals("incomeBgt_bgtGrp_newRealestate", data.get("description"));
        assertEquals(true, data.get("active"));
        assertEquals("BUDGET", data.get("instrumentType"));
        assertEquals("bgtGrp_"+realestateDesc+"@10", data.get("parentBusinesskey"));
        assertEquals(tenantKey, data.get("tenantBusinesskey"));

        var createEvent = new Event(Event.Type.CREATE, realestateKey, newRealEstate);
        saveInstrumentProcessor.accept(createEvent);
        saveInstrumentTreeProcessor.accept(createEvent);


        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(6).verifyComplete();

        StepVerifier.create(instrumentService.listInstruments(tenantKey)).expectNextCount(5).verifyComplete();

    }

    /*@Test
    void createDeprecationObject() {
        setupTestTenant();
        var accPfs = instrumentService.listInstrumentsByType(tenantKey, InstrumentType.ACCOUNTPORTFOLIO).collectList().block();
        assertEquals(1, accPfs.size());
        var accPf = accPfs.get(0);
        assertEquals(accPfKey, accPf.getBusinesskey());
        assertEquals(accPfdesc, accPf.getDescription());
        assertTrue(accPf.isActive());

        var deprecationObject = new Instrument(deprecationObjectDesc, InstrumentType.DEPRECATIONOBJECT);
        deprecationObject.setParentBusinesskey(accPf.getBusinesskey());

        var properties = new HashMap<AdditionalProperties, String>();
        properties.put(AdditionalProperties.VALUEBUDGETID, bgtKey);
        var maturitydate = "2030-01-01";
        properties.put(AdditionalProperties.MATURITYDATE, maturitydate);
        var acquisitiondate = "2025-01-01";
        properties.put(AdditionalProperties.ACQUISITIONDATE, acquisitiondate);
        var acquisitionvalue = "10000";
        properties.put(AdditionalProperties.ACQUISITIONVALUE, acquisitionvalue);
        deprecationObject.setAdditionalProperties(properties);

        instrumentService.saveInstrument(deprecationObject).block();
        final List<String> messages = getMessages("instrumentApproved-out-0");
        assertEquals(1, messages.size());
        LOG.info(messages.get(0));
        
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(deprecationObjectKey, data.get("businesskey"));
        assertEquals(deprecationObjectDesc, data.get("description"));
        assertEquals(true, data.get("active"));
        assertEquals("DEPRECATIONOBJECT", data.get("instrumentType"));
        assertEquals(accPfKey, data.get("parentBusinesskey"));
        assertEquals(tenantKey, data.get("tenantBusinesskey"));
        var propertiesMap = (HashMap)data.get("additionalProperties");
        assertEquals(4, propertiesMap.size());
        assertEquals(bgtKey, (String)propertiesMap.get("VALUEBUDGETID"));
        assertEquals(maturitydate, (String)propertiesMap.get("MATURITYDATE"));
        assertEquals(acquisitiondate, (String)propertiesMap.get("ACQUISITIONDATE"));
        assertEquals(acquisitionvalue, (String)propertiesMap.get("ACQUISITIONVALUE"));


        var createEvent = new Event(Event.Type.CREATE, deprecationObjectKey, deprecationObject);
        saveInstrumentProcessor.accept(createEvent);
        saveInstrumentTreeProcessor.accept(createEvent);


        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(6).verifyComplete();

        StepVerifier.create(instrumentService.listInstruments(tenantKey)).expectNextCount(5).verifyComplete();
    }

    @Test
    void createLifeInsurance() {
        setupTestTenant();
        var accPfs = instrumentService.listInstrumentsByType(tenantKey, InstrumentType.ACCOUNTPORTFOLIO).collectList().block();
        assertEquals(1, accPfs.size());
        var accPf = accPfs.get(0);
        assertEquals(accPfKey, accPf.getBusinesskey());
        assertEquals(accPfdesc, accPf.getDescription());
        assertTrue(accPf.isActive());

        var newLifeInsurance = new Instrument(realestateDesc, InstrumentType.LIFEINSURANCE);
        newLifeInsurance.setParentBusinesskey(accPf.getBusinesskey());

        var surrendervaluedate = "2025-02-10";
        var surrendervaluevalue = "1000";
        var surrendervaluevalues = new HashMap<String, String>();
        surrendervaluevalues.put(surrendervaluedate, surrendervaluevalue);
        Map<AdditionalMaps, Map<String, String>> additionalMaps = new HashMap<>();
        additionalMaps.put(AdditionalMaps.SURRENDERVALUES, surrendervaluevalues);
        newLifeInsurance.setAdditionalMaps(additionalMaps);

        var properties = new HashMap<AdditionalProperties, String>();
        properties.put(AdditionalProperties.VALUEBUDGETID, bgtKey);
        var maturitydate = "2030-02-10";
        properties.put(AdditionalProperties.MATURITYDATE, maturitydate);
        newLifeInsurance.setAdditionalProperties(properties);

        instrumentService.saveInstrument(newLifeInsurance).block();
        final List<String> messages = getMessages("instrumentApproved-out-0");
        assertEquals(1, messages.size());
        LOG.info(messages.get(0));
        
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(lifeInsurenceKey, data.get("businesskey"));
        assertEquals(lifeInsurenceDesc, data.get("description"));
        assertEquals(true, data.get("active"));
        assertEquals("LIFEINSURANCE", data.get("instrumentType"));
        assertEquals(accPfKey, data.get("parentBusinesskey"));
        assertEquals(tenantKey, data.get("tenantBusinesskey"));
        var propertiesMap = (HashMap)data.get("additionalProperties");
        assertEquals(2, propertiesMap.size());
        assertEquals(bgtKey, (String)propertiesMap.get("VALUEBUDGETID"));
        assertEquals(maturitydate, (String)propertiesMap.get("MATURITYDATE"));
        var maps = (HashMap)data.get("additionalMaps");
        assertEquals(1, maps.size());
        assertEquals(surrendervaluevalue, ((HashMap)maps.get("SURRENDERVALUES")).get(surrendervaluedate));


        var createEvent = new Event(Event.Type.CREATE, lifeInsurenceKey, newLifeInsurance);
        saveInstrumentProcessor.accept(createEvent);
        saveInstrumentTreeProcessor.accept(createEvent);


        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(6).verifyComplete();

        StepVerifier.create(instrumentService.listInstruments(tenantKey)).expectNextCount(5).verifyComplete();

    }

    @Test
    void createLoan() {
        setupTestTenant();
        var accPfs = instrumentService.listInstrumentsByType(tenantKey, InstrumentType.ACCOUNTPORTFOLIO).collectList().block();
        assertEquals(1, accPfs.size());
        var accPf = accPfs.get(0);
        assertEquals(accPfKey, accPf.getBusinesskey());
        assertEquals(accPfdesc, accPf.getDescription());
        assertTrue(accPf.isActive());

        var loan = new Instrument(loanDesc, InstrumentType.LOAN);
        loan.setParentBusinesskey(accPf.getBusinesskey());

        var properties = new HashMap<AdditionalProperties, String>();
        var maturitydate = "2030-01-01";
        properties.put(AdditionalProperties.MATURITYDATE, maturitydate);
        var interestrate = "2.5";
        properties.put(AdditionalProperties.INTERESTRATE, interestrate);
        var annuityrate = "1000";
        properties.put(AdditionalProperties.ANNUITYRATE, annuityrate);
        properties.put(AdditionalProperties.REFERENCEGIRO, giroKey);
        loan.setAdditionalProperties(properties);

        instrumentService.saveInstrument(loan).block();
        final List<String> messages = getMessages("instrumentApproved-out-0");
        assertEquals(1, messages.size());
        LOG.info(messages.get(0));
        
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(loanKey, data.get("businesskey"));
        assertEquals(loanDesc, data.get("description"));
        assertEquals(true, data.get("active"));
        assertEquals("LOAN", data.get("instrumentType"));
        assertEquals(accPfKey, data.get("parentBusinesskey"));
        assertEquals(tenantKey, data.get("tenantBusinesskey"));
        var propertiesMap = (HashMap)data.get("additionalProperties");
        assertEquals(4, propertiesMap.size());
        assertEquals(interestrate, (String)propertiesMap.get("INTERESTRATE"));
        assertEquals(maturitydate, (String)propertiesMap.get("MATURITYDATE"));
        assertEquals(annuityrate, (String)propertiesMap.get("ANNUITYRATE"));
        assertEquals(giroKey, (String)propertiesMap.get("REFERENCEGIRO"));


        var createEvent = new Event(Event.Type.CREATE, loanKey, loan);
        saveInstrumentProcessor.accept(createEvent);
        saveInstrumentTreeProcessor.accept(createEvent);


        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(6).verifyComplete();

        StepVerifier.create(instrumentService.listInstruments(tenantKey)).expectNextCount(5).verifyComplete();
    }

    @Test
    void createMoneyAtCall() {
        setupTestTenant();
        var accPfs = instrumentService.listInstrumentsByType(tenantKey, InstrumentType.ACCOUNTPORTFOLIO).collectList().block();
        assertEquals(1, accPfs.size());
        var accPf = accPfs.get(0);
        assertEquals(accPfKey, accPf.getBusinesskey());
        assertEquals(accPfdesc, accPf.getDescription());
        assertTrue(accPf.isActive());

        var instrument = new Instrument(moneyAtCallDesc, InstrumentType.MONEYATCALL);
        instrument.setParentBusinesskey(accPf.getBusinesskey());

        var properties = new HashMap<AdditionalProperties, String>();
        var iban = "de0000000001";
        properties.put(AdditionalProperties.IBAN, iban);
        instrument.setAdditionalProperties(properties);

        instrumentService.saveInstrument(instrument).block();
        final List<String> messages = getMessages("instrumentApproved-out-0");
        assertEquals(1, messages.size());
        LOG.info(messages.get(0));
        
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(moneyAtCallKey, data.get("businesskey"));
        assertEquals(moneyAtCallDesc, data.get("description"));
        assertEquals(true, data.get("active"));
        assertEquals("MONEYATCALL", data.get("instrumentType"));
        assertEquals(accPfKey, data.get("parentBusinesskey"));
        assertEquals(tenantKey, data.get("tenantBusinesskey"));
        var propertiesMap = (HashMap)data.get("additionalProperties");
        assertEquals(4, propertiesMap.size());
        assertEquals(iban, (String)propertiesMap.get("IBAN"));


        var createEvent = new Event(Event.Type.CREATE, loanKey, instrument);
        saveInstrumentProcessor.accept(createEvent);
        saveInstrumentTreeProcessor.accept(createEvent);


        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(6).verifyComplete();

        StepVerifier.create(instrumentService.listInstruments(tenantKey)).expectNextCount(5).verifyComplete();
    }

    @Test
    void createTimeDeposit() {
        setupTestTenant();
        var accPfs = instrumentService.listInstrumentsByType(tenantKey, InstrumentType.ACCOUNTPORTFOLIO).collectList().block();
        assertEquals(1, accPfs.size());
        var accPf = accPfs.get(0);
        assertEquals(accPfKey, accPf.getBusinesskey());
        assertEquals(accPfdesc, accPf.getDescription());
        assertTrue(accPf.isActive());

        var instrument = new Instrument(timeDepositDesc, InstrumentType.TIMEDEPOSIT);
        instrument.setParentBusinesskey(accPf.getBusinesskey());

        var properties = new HashMap<AdditionalProperties, String>();
        var iban = "de0000000001";
        properties.put(AdditionalProperties.IBAN, iban);
        instrument.setAdditionalProperties(properties);

        instrumentService.saveInstrument(instrument).block();
        final List<String> messages = getMessages("instrumentApproved-out-0");
        assertEquals(1, messages.size());
        LOG.info(messages.get(0));
        
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(timeDepositKey, data.get("businesskey"));
        assertEquals(timeDepositDesc, data.get("description"));
        assertEquals(true, data.get("active"));
        assertEquals("TIMEDEPOSIT", data.get("instrumentType"));
        assertEquals(accPfKey, data.get("parentBusinesskey"));
        assertEquals(tenantKey, data.get("tenantBusinesskey"));
        var propertiesMap = (HashMap)data.get("additionalProperties");
        assertEquals(4, propertiesMap.size());
        assertEquals(iban, (String)propertiesMap.get("IBAN"));


        var createEvent = new Event(Event.Type.CREATE, loanKey, instrument);
        saveInstrumentProcessor.accept(createEvent);
        saveInstrumentTreeProcessor.accept(createEvent);


        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(6).verifyComplete();

        StepVerifier.create(instrumentService.listInstruments(tenantKey)).expectNextCount(5).verifyComplete();
    }

    @Test
    void createBuildingSavingAccount() {
        setupTestTenant();
        var accPfs = instrumentService.listInstrumentsByType(tenantKey, InstrumentType.ACCOUNTPORTFOLIO).collectList().block();
        assertEquals(1, accPfs.size());
        var accPf = accPfs.get(0);
        assertEquals(accPfKey, accPf.getBusinesskey());
        assertEquals(accPfdesc, accPf.getDescription());
        assertTrue(accPf.isActive());

        var instrument = new Instrument(buildingsavingAccountDesc, InstrumentType.BUILDINGSAVINGACCOUNT);
        instrument.setParentBusinesskey(accPf.getBusinesskey());

        var properties = new HashMap<AdditionalProperties, String>();
        var iban = "de0000000001";
        properties.put(AdditionalProperties.IBAN, iban);
        instrument.setAdditionalProperties(properties);

        instrumentService.saveInstrument(instrument).block();
        final List<String> messages = getMessages("instrumentApproved-out-0");
        assertEquals(1, messages.size());
        LOG.info(messages.get(0));
        
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(buildingsavingAccountKey, data.get("businesskey"));
        assertEquals(buildingsavingAccountDesc, data.get("description"));
        assertEquals(true, data.get("active"));
        assertEquals("BUILDINGSAVINGACCOUNT", data.get("instrumentType"));
        assertEquals(accPfKey, data.get("parentBusinesskey"));
        assertEquals(tenantKey, data.get("tenantBusinesskey"));
        var propertiesMap = (HashMap)data.get("additionalProperties");
        assertEquals(4, propertiesMap.size());
        assertEquals(iban, (String)propertiesMap.get("IBAN"));


        var createEvent = new Event(Event.Type.CREATE, loanKey, instrument);
        saveInstrumentProcessor.accept(createEvent);
        saveInstrumentTreeProcessor.accept(createEvent);


        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(6).verifyComplete();

        StepVerifier.create(instrumentService.listInstruments(tenantKey)).expectNextCount(5).verifyComplete();
    }

    @Test
    void createEtf() {
        var isin = "de0000000001";
        var properties = new HashMap<AdditionalProperties, String>();
        properties.put(AdditionalProperties.ISIN, isin);
        var instrument = new Instrument(etfDesc, InstrumentType.ETF);
        instrument.setAdditionalProperties(properties);

        instrumentService.saveInstrument(instrument).block();
        var messages = getMessages("instrumentApproved-out-0");
        assertEquals(1, messages.size());
        LOG.info(messages.get(0));
        Event createEvent = new Event(Event.Type.CREATE, isin, instrument);
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(etfKey, data.get("businesskey"));
        assertEquals(etfDesc, data.get("description"));
        assertEquals(true, data.get("active"));
        assertEquals("ETF", data.get("instrumentType"));
        assertNull(data.get("parentBusinesskey"));
        assertNull(data.get("tenantBusinesskey"));

        var propertiesMap = (HashMap)data.get("additionalProperties");
        assertEquals(1, propertiesMap.size());
        assertEquals(isin.toUpperCase(), (String)propertiesMap.get("ISIN"));

        saveInstrumentProcessor.accept(createEvent);
        saveInstrumentTreeProcessor.accept(createEvent);
        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(2).verifyComplete();
    }

    @Test
    void createBond() {
        var isin = "de0000000001";
        var properties = new HashMap<AdditionalProperties, String>();
        properties.put(AdditionalProperties.ISIN, isin);
        var instrument = new Instrument(bondDesc, InstrumentType.BOND);
        instrument.setAdditionalProperties(properties);

        instrumentService.saveInstrument(instrument).block();
        var messages = getMessages("instrumentApproved-out-0");
        assertEquals(1, messages.size());
        LOG.info(messages.get(0));
        Event createEvent = new Event(Event.Type.CREATE, isin, instrument);
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(bondKey, data.get("businesskey"));
        assertEquals(bondDesc, data.get("description"));
        assertEquals(true, data.get("active"));
        assertEquals("ETF", data.get("instrumentType"));
        assertNull(data.get("parentBusinesskey"));
        assertNull(data.get("tenantBusinesskey"));

        var propertiesMap = (HashMap)data.get("additionalProperties");
        assertEquals(1, propertiesMap.size());
        assertEquals(isin.toUpperCase(), (String)propertiesMap.get("ISIN"));

        saveInstrumentProcessor.accept(createEvent);
        saveInstrumentTreeProcessor.accept(createEvent);
        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(2).verifyComplete();
    }

    @Test
    void createFond() {
        var isin = "de0000000001";
        var properties = new HashMap<AdditionalProperties, String>();
        properties.put(AdditionalProperties.ISIN, isin);
        var instrument = new Instrument(bondDesc, InstrumentType.FONDS);
        instrument.setAdditionalProperties(properties);

        instrumentService.saveInstrument(instrument).block();
        var messages = getMessages("instrumentApproved-out-0");
        assertEquals(1, messages.size());
        LOG.info(messages.get(0));
        Event createEvent = new Event(Event.Type.CREATE, isin, instrument);
        JsonHelper jsonHelper = new JsonHelper();
        var data = (LinkedHashMap)jsonHelper.convertJsonStringToMap((messages.get(0))).get("data");
        assertEquals(fondKey, data.get("businesskey"));
        assertEquals(fondDesc, data.get("description"));
        assertEquals(true, data.get("active"));
        assertEquals("ETF", data.get("instrumentType"));
        assertNull(data.get("parentBusinesskey"));
        assertNull(data.get("tenantBusinesskey"));

        var propertiesMap = (HashMap)data.get("additionalProperties");
        assertEquals(1, propertiesMap.size());
        assertEquals(isin.toUpperCase(), (String)propertiesMap.get("ISIN"));

        saveInstrumentProcessor.accept(createEvent);
        saveInstrumentTreeProcessor.accept(createEvent);
        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(2).verifyComplete();
    }*/
}
