package de.hf.myfinance.instruments;

import de.hf.myfinance.instruments.persistence.entities.InstrumentEntity;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.restmodel.InstrumentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@DataJpaTest
public class PersistenceTests{

    /*@Autowired
    private InstrumentRepository repository;

    @Test
    void create() {

        var newEntity = new InstrumentEntity(InstrumentType.TENANT, "aTest", true, LocalDateTime.now());
        repository.save(newEntity);

        var foundEntity = repository.findById(newEntity.getInstrumentid()).get();
        assertEqualsTenant(newEntity, foundEntity);

        assertEquals(1, repository.count());
    }

    private void assertEqualsTenant(InstrumentEntity expectedEntity, InstrumentEntity actualEntity) {
        assertEquals(expectedEntity.getInstrumentid(),        actualEntity.getInstrumentid());
        assertEquals(expectedEntity.getInstrumentType(),        actualEntity.getInstrumentType());
        assertEquals(expectedEntity.getBusinesskey(),   actualEntity.getBusinesskey());
        assertEquals(expectedEntity.getDescription(), actualEntity.getDescription());
        assertEquals(expectedEntity.isIsactive(),  actualEntity.isIsactive());
        assertEquals(expectedEntity.getTreelastchanged(),    actualEntity.getTreelastchanged());
    }*/
}
