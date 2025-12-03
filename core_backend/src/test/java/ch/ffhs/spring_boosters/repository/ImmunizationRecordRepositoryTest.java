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
@TestPropertySource(properties = {"spring.flyway.enabled=false"})
class ImmunizationRecordRepositoryTest {

    @Autowired
    private ImmunizationRecordRepository repository;

    @Test
    void saveAndQueryAndDeleteByUserAndId() {
        UUID userId = UUID.randomUUID();
        UUID vt = UUID.randomUUID();
        UUID plan = UUID.randomUUID();

        ImmunizationRecord rec = new ImmunizationRecord(userId, vt, LocalDate.now());
        rec.setImmunizationPlanId(plan);
        ImmunizationRecord saved = repository.save(rec);

        assertNotNull(saved.getId());

        List<ImmunizationRecord> byUser = repository.findByUserId(userId);
        assertFalse(byUser.isEmpty(), "Should find record by userId");

        List<ImmunizationRecord> byVt = repository.findByVaccineTypeId(vt);
        assertFalse(byVt.isEmpty(), "Should find record by vaccineTypeId");

        List<ImmunizationRecord> byPlan = repository.findByImmunizationPlanId(plan);
        assertFalse(byPlan.isEmpty(), "Should find record by immunizationPlanId");

        List<ImmunizationRecord> byUserAndVt = repository.findByUserIdAndVaccineTypeId(userId, vt);
        assertFalse(byUserAndVt.isEmpty(), "Should find record by userId and vaccineTypeId");

        boolean exists = repository.existsByUserIdAndId(userId, saved.getId());
        assertTrue(exists, "Record should exist");

        repository.deleteByUserIdAndId(userId, saved.getId());
        boolean existsAfter = repository.existsByUserIdAndId(userId, saved.getId());
        assertFalse(existsAfter, "Record should be deleted");
    }
}
