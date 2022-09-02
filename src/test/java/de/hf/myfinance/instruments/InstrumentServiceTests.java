package de.hf.myfinance.instruments;

import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.service.InstrumentService;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;


import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Testcontainers
class InstrumentServiceTests extends MongoDbTestBase{

    @Autowired
    InstrumentService instrumentService;
    @Autowired
    InstrumentRepository instrumentRepository;
    @Autowired
    InstrumentGraphRepository instrumentGraphRepository;

    @BeforeEach
    void setupDb() {
        instrumentRepository.deleteAll().block();
        instrumentGraphRepository.deleteAll().block();
    }

    @Test
    void createTenant() {
        var tenantKey = "aTest@6";
        var tenantDesc = "aTest";
        var newTenant = new Instrument(tenantDesc, InstrumentType.TENANT);
        instrumentService.addInstrument(newTenant).block();
        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(5).verifyComplete();

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

        var budgetPfs = instrumentService.listInstrumentsByType(tenantKey, InstrumentType.BUDGETPORTFOLIO).collectList().block();
        assertEquals(1, budgetPfs.size());
        var budgetPf = budgetPfs.get(0);
        assertEquals("bgtPf_aTest@23", budgetPf.getBusinesskey());
        assertEquals("bgtPf_aTest", budgetPf.getDescription());
        assertTrue(budgetPf.isIsactive());

        var budgetgroups = instrumentService.listInstrumentsByType(tenantKey, InstrumentType.BUDGETGROUP).collectList().block();
        assertEquals(1, budgetgroups.size());
        var budgetgroup = budgetgroups.get(0);
        assertEquals("bgtGrp_aTest@10", budgetgroup.getBusinesskey());
        assertEquals("bgtGrp_aTest", budgetgroup.getDescription());
        assertTrue(budgetgroup.isIsactive());

        var budgets = instrumentService.listInstrumentsByType(tenantKey, InstrumentType.BUDGET).collectList().block();
        assertEquals(1, budgets.size());
        var budget = budgets.get(0);
        assertEquals("incomeBgt_bgtGrp_aTest@5", budget.getBusinesskey());
        assertEquals("incomeBgt_bgtGrp_aTest", budget.getDescription());
        assertTrue(budget.isIsactive());

        StepVerifier.create(instrumentService.listInstruments(tenantKey)).expectNextCount(4).verifyComplete();
    }

    @Test
    void createDuplicate() {
        var tenantKey = "aTest@6";
        var tenantDesc = "aTest";
        var newTenant = new Instrument(tenantDesc, InstrumentType.TENANT);
        instrumentService.addInstrument(newTenant).block();
        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(5).verifyComplete();

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
    }

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
        StepVerifier.create(instrumentService.listInstruments()).expectNextCount(5).verifyComplete();

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
}
