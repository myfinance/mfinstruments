package de.hf.myfinance.instruments;

import de.hf.framework.audit.AuditService;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.service.InstrumentFactory;
import de.hf.myfinance.instruments.service.InstrumentService;
import de.hf.myfinance.restmodel.InstrumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@Testcontainers
public class TenantTests  extends MongoDbTestBase{

    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private InstrumentGraphRepository instrumentGraphRepository;
    @Autowired
    private InstrumentFactory instrumentFactory;

    @BeforeEach
    void setupDb() {
        instrumentRepository.deleteAll();
        instrumentGraphRepository.deleteAll();
    }

    @Test
    void create() {
        instrumentService.newTenant("aTest");
        var instruments = instrumentService.listInstruments();
        assertEquals(5, instruments.size());

        var tenant = instruments.stream().filter(i->i.getInstrumentType().equals(InstrumentType.TENANT)).findFirst();
        assertTrue(tenant.isPresent());
        assertEquals("aTest", tenant.get().getBusinesskey());
        assertEquals("aTest", tenant.get().getDescription());
        assertTrue(tenant.get().isIsactive());

        var accountpf = instruments.stream().filter(i->i.getInstrumentType().equals(InstrumentType.ACCOUNTPORTFOLIO)).findFirst();
        assertTrue(accountpf.isPresent());
        assertEquals("accountPf_aTest", accountpf.get().getBusinesskey());
        assertEquals("accountPf_aTest", accountpf.get().getDescription());
        assertTrue(accountpf.get().isIsactive());

        var budgetpf = instruments.stream().filter(i->i.getInstrumentType().equals(InstrumentType.BUDGETPORTFOLIO)).findFirst();
        assertTrue(budgetpf.isPresent());
        assertEquals("budgetPf_aTest", budgetpf.get().getBusinesskey());
        assertEquals("budgetPf_aTest", budgetpf.get().getDescription());
        assertTrue(budgetpf.get().isIsactive());

        var budgetgroup = instruments.stream().filter(i->i.getInstrumentType().equals(InstrumentType.BUDGETGROUP)).findFirst();
        assertTrue(budgetgroup.isPresent());
        assertEquals("budgetGroup_aTest", budgetgroup.get().getBusinesskey());
        assertEquals("budgetGroup_aTest", budgetgroup.get().getDescription());
        assertTrue(budgetgroup.get().isIsactive());

        var budget = instruments.stream().filter(i->i.getInstrumentType().equals(InstrumentType.BUDGET)).findFirst();
        assertTrue(budget.isPresent());
        assertEquals("incomeBudget_budgetGroup_aTest", budget.get().getBusinesskey());
        assertEquals("incomeBudget_budgetGroup_aTest", budget.get().getDescription());
        assertTrue(budget.get().isIsactive());
    }
}
