package de.hf.myfinance.instruments;

import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.restmodel.InstrumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataMongoTest
@Testcontainers
public class PersistenceTests extends MongoDbTestBase{

    @Autowired
    private InstrumentRepository repository;

    @BeforeEach
    void setupDb() {
        repository.deleteAll();
    }

    @Test
    void create() {
        var newEntity = new InstrumentEntity(InstrumentType.TENANT, "aTest", true, LocalDateTime.now());
        repository.save(newEntity);

        var foundEntity = repository.findById(newEntity.getInstrumentid()).get();
        assertEqualsTenant(newEntity, foundEntity);

        assertEquals(1, repository.count());
        repository.deleteAll();
        assertEquals(0, repository.count());
    }

    private void assertEqualsTenant(InstrumentEntity expectedEntity, InstrumentEntity actualEntity) {
        assertEquals(expectedEntity.getInstrumentid(),        actualEntity.getInstrumentid());
        assertEquals(expectedEntity.getInstrumentType(),        actualEntity.getInstrumentType());
        assertEquals(expectedEntity.getBusinesskey(),   actualEntity.getBusinesskey());
        assertEquals(expectedEntity.getDescription(), actualEntity.getDescription());
        assertEquals(expectedEntity.isIsactive(),  actualEntity.isIsactive());
        assertEquals(expectedEntity.getTreelastchanged().getSecond(),    actualEntity.getTreelastchanged().getSecond());
    }

    @Test
    void optimisticLockError() {

        var newEntity = new InstrumentEntity(InstrumentType.TENANT, "aTest", true, LocalDateTime.now());
        repository.save(newEntity);

        // Store the saved entity in two separate entity objects
        var foundEntity1 = repository.findById(newEntity.getInstrumentid()).get();
        var foundEntity2 = repository.findById(newEntity.getInstrumentid()).get();

        // Update the entity using the first entity object
        foundEntity1.setDescription("n1");
        repository.save(foundEntity1);

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds an old version
        // number, that is, an Optimistic Lock Error
        assertThrows(OptimisticLockingFailureException.class, () -> {
            foundEntity2.setDescription("n2");
            repository.save(foundEntity2);
        });

        // Get the updated entity from the database and verify its new state
        var updatedEntity =
                repository.findById(newEntity.getInstrumentid()).get();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("n1", updatedEntity.getDescription());
    }
}
