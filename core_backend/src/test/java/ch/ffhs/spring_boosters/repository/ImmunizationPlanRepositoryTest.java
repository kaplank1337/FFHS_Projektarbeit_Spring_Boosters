package ch.ffhs.spring_boosters.repository;

import ch.ffhs.spring_boosters.controller.entity.ImmunizationPlan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.flyway.enabled=false")
class ImmunizationPlanRepositoryTest {

    @Autowired
    private ImmunizationPlanRepository repository;

    @Test
    void saveAndQueries() {
        UUID vt = UUID.randomUUID();
        UUID ac = UUID.randomUUID();

        ImmunizationPlan plan = new ImmunizationPlan("Plan X", vt, ac);
        ImmunizationPlan saved = repository.save(plan);

        assertNotNull(saved.getId());

        Optional<ImmunizationPlan> found = repository.findByName("Plan X");
        assertTrue(found.isPresent());

        assertTrue(repository.existsByName("Plan X"));

        List<ImmunizationPlan> byVt = repository.findByVaccineTypeId(vt);
        assertFalse(byVt.isEmpty());

        List<ImmunizationPlan> byAc = repository.findByAgeCategoryId(ac);
        assertFalse(byAc.isEmpty());
    }
}
