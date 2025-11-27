package ch.ffhs.spring_boosters.repository;

import ch.ffhs.spring_boosters.controller.entity.VaccineType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.flyway.enabled=false")
class VaccineTypeRepositoryTest {

    @Autowired
    private VaccineTypeRepository repository;

    @Test
    void saveAndFindAll() {
        VaccineType v = new VaccineType("Moderna", "COVID-MOD");
        VaccineType saved = repository.save(v);
        assertNotNull(saved.getId());

        List<VaccineType> all = repository.findAll();
        assertFalse(all.isEmpty());
    }
}
