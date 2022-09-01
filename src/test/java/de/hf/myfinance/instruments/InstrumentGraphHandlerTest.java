package de.hf.myfinance.instruments;

import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.service.instrumentgraphhandler.InstrumentGraphHandlerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertTrue;


@DataMongoTest
@Testcontainers
public class InstrumentGraphHandlerTest extends MongoDbTestBase{

    @Autowired
    InstrumentGraphHandlerImpl instrumentGraphHandler;

    @Autowired
    InstrumentGraphRepository instrumentGraphRepository;

    @BeforeEach
    void setupDb() {
        instrumentGraphRepository.deleteAll().block();
    }

    @Test
    void listChilds() {
        var tenantKey = "aTest@6";
        var accPfKey = "accPf@8";
        var budgetPfKey = "budgetPf@23";


        instrumentGraphHandler.addInstrumentToGraph(tenantKey, tenantKey).block();
        instrumentGraphHandler.addInstrumentToGraph(accPfKey, tenantKey).block();
        instrumentGraphHandler.addInstrumentToGraph(budgetPfKey, tenantKey).block();

        StepVerifier.create(instrumentGraphRepository.findAll()).expectNextCount(5).verifyComplete();
        StepVerifier.create(instrumentGraphHandler.getInstrumentChildIds(tenantKey, EdgeType.TENANTGRAPH)).expectNextCount(2).verifyComplete();
        var result = instrumentGraphHandler.getInstrumentChildIds(tenantKey, EdgeType.TENANTGRAPH).collectList().block();
        assertTrue(result.contains(budgetPfKey));
        assertTrue(result.contains(accPfKey));
    }
}
