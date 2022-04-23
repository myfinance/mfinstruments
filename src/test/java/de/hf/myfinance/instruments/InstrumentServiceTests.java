package de.hf.myfinance.instruments;

import de.hf.framework.exceptions.MFException;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.service.InstrumentService;
import de.hf.myfinance.restmodel.InstrumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.testcontainers.junit.jupiter.Testcontainers;

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
        instrumentRepository.deleteAll();
        instrumentGraphRepository.deleteAll();
    }

    @Test
    void createTenant() {
        instrumentService.newTenant("aTest");
        var instruments = instrumentService.listInstruments();
        assertEquals(5, instruments.size());

        var tenant = instruments.stream().filter(i->i.getInstrumentType().equals(InstrumentType.TENANT)).findFirst();
        assertTrue(tenant.isPresent());
        assertEquals("aTest@6", tenant.get().getBusinesskey());
        assertEquals("aTest", tenant.get().getDescription());
        assertTrue(tenant.get().isIsactive());

        var accountpf = instruments.stream().filter(i->i.getInstrumentType().equals(InstrumentType.ACCOUNTPORTFOLIO)).findFirst();
        assertTrue(accountpf.isPresent());
        assertEquals("accPf_aTest@8", accountpf.get().getBusinesskey());
        assertEquals("accPf_aTest", accountpf.get().getDescription());
        assertTrue(accountpf.get().isIsactive());

        var budgetpf = instruments.stream().filter(i->i.getInstrumentType().equals(InstrumentType.BUDGETPORTFOLIO)).findFirst();
        assertTrue(budgetpf.isPresent());
        assertEquals("bgtPf_aTest@23", budgetpf.get().getBusinesskey());
        assertEquals("bgtPf_aTest", budgetpf.get().getDescription());
        assertTrue(budgetpf.get().isIsactive());

        var budgetgroup = instruments.stream().filter(i->i.getInstrumentType().equals(InstrumentType.BUDGETGROUP)).findFirst();
        assertTrue(budgetgroup.isPresent());
        assertEquals("bgtGrp_aTest@10", budgetgroup.get().getBusinesskey());
        assertEquals("bgtGrp_aTest", budgetgroup.get().getDescription());
        assertTrue(budgetgroup.get().isIsactive());

        var budget = instruments.stream().filter(i->i.getInstrumentType().equals(InstrumentType.BUDGET)).findFirst();
        assertTrue(budget.isPresent());
        assertEquals("incomeBgt_bgtGrp_aTest@5", budget.get().getBusinesskey());
        assertEquals("incomeBgt_bgtGrp_aTest", budget.get().getDescription());
        assertTrue(budget.get().isIsactive());

        var instrument4Tenant = instrumentService.listInstruments(tenant.get().getBusinesskey());
        assertEquals(4, instrument4Tenant.size());
    }

    @Test
    void createDuplicate() {
        instrumentService.newTenant("aTest");
        var instruments = instrumentService.listInstruments();
        assertEquals(5, instruments.size());

        var tenant = instruments.stream().filter(i->i.getInstrumentType().equals(InstrumentType.TENANT)).findFirst();
        assertTrue(tenant.isPresent());
        assertEquals("aTest@6", tenant.get().getBusinesskey());
        assertEquals("aTest", tenant.get().getDescription());
        assertTrue(tenant.get().isIsactive());

        instrumentService.newTenant("aTest");
        instruments = instrumentService.listInstruments();
        assertEquals(5, instruments.size());

        tenant = instruments.stream().filter(i->i.getInstrumentType().equals(InstrumentType.TENANT)).findFirst();
        assertTrue(tenant.isPresent());
        assertEquals("aTest@6", tenant.get().getBusinesskey());
        assertEquals("aTest", tenant.get().getDescription());
        assertTrue(tenant.get().isIsactive());
    }

    @Test
    void createInstrumentHandlerWithInvalidBusinesskey() {
        assertThrows(MFException.class, () -> {
            instrumentService.getInstrument("bla");
        });
    }

    @Test
    void updateTenant() {
        instrumentService.newTenant("aTest");
        var instruments = instrumentService.listInstruments();
        assertEquals(5, instruments.size());

        var tenant = instruments.stream().filter(i->i.getInstrumentType().equals(InstrumentType.TENANT)).findFirst();
        assertTrue(tenant.isPresent());
        assertEquals("aTest@6", tenant.get().getBusinesskey());
        assertEquals("aTest", tenant.get().getDescription());
        assertTrue(tenant.get().isIsactive());

        instrumentService.updateInstrument("aTest@6", "newTenantDesc", true);
        instruments = instrumentService.listInstruments();
        assertEquals(5, instruments.size());

        tenant = instruments.stream().filter(i->i.getInstrumentType().equals(InstrumentType.TENANT)).findFirst();
        assertTrue(tenant.isPresent());
        assertEquals("aTest@6", tenant.get().getBusinesskey());
        assertEquals("newTenantDesc", tenant.get().getDescription());
        assertTrue(tenant.get().isIsactive());
    }
}
