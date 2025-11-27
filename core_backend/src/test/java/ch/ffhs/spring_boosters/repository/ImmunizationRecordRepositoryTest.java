package ch.ffhs.spring_boosters.repository;

import ch.ffhs.spring_boosters.controller.entity.ImmunizationRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.flyway.enabled=false")
class ImmunizationRecordRepositoryTest {

    @Autowired
    private ImmunizationRecordRepository repository;

    @Test
    void saveAndQueryAndDeleteByUserAndId() {
        UUID userId = UUID.randomUUID();
        UUID vt = UUID.randomUUID();
        UUID plan = UUID.randomUUID();

        ImmunizationRecord rec = new ImmunizationRecord(userId, vt, plan, LocalDate.now());
        ImmunizationRecord saved = repository.save(rec);

        assertNotNull(saved.getId());

        List<ImmunizationRecord> byUser = repository.findByUserId(userId);
        assertFalse(byUser.isEmpty());

        List<ImmunizationRecord> byVt = repository.findByVaccineTypeId(vt);
        assertFalse(byVt.isEmpty());

        List<ImmunizationRecord> byPlan = repository.findByImmunizationPlanId(plan);
        assertFalse(byPlan.isEmpty());

        List<ImmunizationRecord> byUserAndVt = repository.findByUserIdAndVaccineTypeId(userId, vt);
        assertFalse(byUserAndVt.isEmpty());

        boolean exists = repository.existsByUserIdAndId(userId, saved.getId());
        assertTrue(exists);

        repository.deleteByUserIdAndId(userId, saved.getId());
        boolean existsAfter = repository.existsByUserIdAndId(userId, saved.getId());
        assertFalse(existsAfter);
    }
}
