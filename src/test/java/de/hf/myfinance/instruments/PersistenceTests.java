package de.hf.myfinance.instruments;

import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.restmodel.InstrumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
class PersistenceTests extends MongoDbTestBase{

    @Autowired
    InstrumentRepository repository;

    @BeforeEach
    void setupDb() {
        repository.deleteAll().block();
    }

    @Test
    void create() throws Exception {
        var newEntity = new InstrumentEntity(InstrumentType.TENANT, "aTest", true, LocalDateTime.now());
        var newEntityMono = repository.save(newEntity).block();

        if(newEntityMono==null || newEntityMono.getInstrumentid()==null) {
            throw new Exception("newEntityMono could not be saved");
        }
        var foundEntity = repository.findById(newEntityMono.getInstrumentid()).block();
        if(foundEntity==null) {
            throw new Exception("no entity found");
        }
        assertEqualsTenant(newEntity, foundEntity);

        assertEquals(1, repository.count().block());
        repository.deleteAll().block();
        assertEquals(0, repository.count().block());
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
    void optimisticLockError() throws Exception {

        var newEntity = new InstrumentEntity(InstrumentType.TENANT, "aTest", true, LocalDateTime.now());
        newEntity = repository.save(newEntity).block();

        if(newEntity==null || newEntity.getInstrumentid()==null) {
            throw new Exception("newEntityMono could not be saved");
        }

        // Store the saved entity in two separate entity objects
        var foundEntity1 = repository.findById(newEntity.getInstrumentid()).block();
        var foundEntity2 = repository.findById(newEntity.getInstrumentid()).block();

        if(foundEntity1==null || foundEntity2==null) {
            throw new Exception("newEntity could not be saved");
        }

        // Update the entity using the first entity object
        foundEntity1.setDescription("n1");
        repository.save(foundEntity1).block();

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds an old version
        // number, that is, an Optimistic Lock Error
        foundEntity2.setDescription("n2");
        var result = repository.save(foundEntity2);
        assertThrows(OptimisticLockingFailureException.class, result::block);

        // Get the updated entity from the database and verify its new state
        var updatedEntity =
                repository.findById(newEntity.getInstrumentid()).block();
        if(updatedEntity==null || updatedEntity.getVersion()==null) {
            throw new Exception(".getVersion() could not be found");
        }

        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("n1", updatedEntity.getDescription());
    }
}
