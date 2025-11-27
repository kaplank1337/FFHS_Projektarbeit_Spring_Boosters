package ch.ffhs.spring_boosters.repository;

import ch.ffhs.spring_boosters.controller.entity.ActiveSubstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class ActiveSubstanceRepositoryTest {

    @Autowired
    private ActiveSubstanceRepository repository;

    @Test
    void saveAndFindByNameAndExists() {
        ActiveSubstance as = new ActiveSubstance("Paracetamol", new String[]{"Aceta"});
        ActiveSubstance saved = repository.save(as);

        assertNotNull(saved.getId());

        Optional<ActiveSubstance> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Paracetamol", found.get().getName());

        boolean exists = repository.existsByName("Paracetamol");
        assertTrue(exists);
    }
}
