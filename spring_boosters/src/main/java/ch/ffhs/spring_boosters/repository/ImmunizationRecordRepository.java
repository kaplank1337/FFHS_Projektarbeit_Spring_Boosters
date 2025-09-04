package ch.ffhs.spring_boosters.repository;

import ch.ffhs.spring_boosters.controller.entity.ImmunizationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImmunizationRecordRepository extends JpaRepository<ImmunizationRecord, UUID> {
    List<ImmunizationRecord> findByUserId(UUID userId);
    List<ImmunizationRecord> findByVaccineTypeId(UUID vaccineTypeId);
    List<ImmunizationRecord> findByImmunizationPlanId(UUID immunizationPlanId);
    List<ImmunizationRecord> findByUserIdAndVaccineTypeId(UUID userId, UUID vaccineTypeId);
}
