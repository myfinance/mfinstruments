package de.hf.myfinance.instruments;

import de.hf.myfinance.instruments.persistence.entities.EdgeType;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentGraphRepository;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.instruments.service.accountableinstrumenthandler.TenantHandler;
import de.hf.myfinance.instruments.service.environment.InstrumentEnvironmentWithFactory;
import de.hf.myfinance.instruments.service.instrumentgraphhandler.InstrumentGraphHandlerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@Import({TestChannelBinderConfiguration.class})
class AccountableInstrumentHandlerTest extends MongoDbTestBase{
    @Autowired
    InstrumentGraphRepository instrumentGraphRepository;

    @Autowired
    InstrumentRepository instrumentRepository;

    @Autowired
    InstrumentEnvironmentWithFactory instrumentEnvironment;

    @Autowired
    InstrumentGraphHandlerImpl instrumentGraphHandler;

    @BeforeEach
    void setupDb() {
        instrumentRepository.deleteAll().block();
        instrumentGraphRepository.deleteAll().block();
    }


    @Test
    void listChilds() {
        var tenantKey = "aTest@6";

        var tenantHandler = new TenantHandler(instrumentEnvironment, "aTest", null, true);
        tenantHandler.save().block();
        StepVerifier.create(instrumentGraphRepository.findAll()).expectNextCount(12).verifyComplete();

        var test = instrumentGraphRepository.findAll().collectList().block();
        test.forEach(i-> System.out.println("vater:"+i.getAncestor() + ", kind:"+ i.getDescendant() + ", tiefe:" + i.getPathlength()));

        var aresult = instrumentGraphHandler.getInstrumentChildIds(tenantKey, EdgeType.TENANTGRAPH, 0).collectList().block();
        assertEquals(4, aresult.size());


        var nextresult = instrumentGraphHandler.getInstrumentChildIds(tenantKey, EdgeType.TENANTGRAPH, 0).reduce(new ArrayList<String>(), (result, element) -> {
            result.add(element);
            return result;
        }).block();

        nextresult.forEach(i-> System.out.println("id:"+i));

        assertEquals(4, nextresult.size());

        var allinstruments = instrumentRepository.findAll().collectList().block();
        allinstruments.forEach(i->  System.out.println("instrument:"+i.getBusinesskey()));


        var nextnextresult = instrumentGraphHandler.getInstrumentChildIds(tenantKey, EdgeType.TENANTGRAPH, 0).reduce(new ArrayList<String>(), (result, element) -> {
            result.add(element);
            return result;
        }).flatMapMany(businessKeys ->
                instrumentRepository.findByBusinesskeyIn(businessKeys)).collectList().block();

        assertEquals(4, nextnextresult.size());

        var childs = tenantHandler.listInstrumentChilds().collectList().block();
        assertEquals(4, childs.size());

    }
}
