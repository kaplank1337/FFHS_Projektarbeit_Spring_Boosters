package ch.ffhs.spring_boosters.repository;

import ch.ffhs.spring_boosters.controller.entity.AgeCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class AgeCategoryRepositoryTest {

    @Autowired
    private AgeCategoryRepository repository;

    @Test
    void saveFindByNameAndExists() {
        AgeCategory ac = new AgeCategory("Säugling", 0, 365);
        AgeCategory saved = repository.save(ac);

        assertNotNull(saved.getId());

        Optional<AgeCategory> found = repository.findByName("Säugling");
        assertTrue(found.isPresent());
        assertEquals(0, found.get().getAgeMinDays());

        assertTrue(repository.existsByName("Säugling"));
    }
}
