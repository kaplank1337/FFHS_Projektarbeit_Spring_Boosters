package ch.ffhs.spring_boosters.repository;

import ch.ffhs.spring_boosters.controller.entity.ImmunizationRecord;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImmunizationRecordRepository extends JpaRepository<ImmunizationRecord, UUID> {
    @EntityGraph(attributePaths = {"user", "vaccineType", "immunizationPlan"})
    List<ImmunizationRecord> findByUserId(UUID userId);

    @EntityGraph(attributePaths = {"user", "vaccineType", "immunizationPlan"})
    List<ImmunizationRecord> findByVaccineTypeId(UUID vaccineTypeId);

    @EntityGraph(attributePaths = {"user", "vaccineType", "immunizationPlan"})
    List<ImmunizationRecord> findByImmunizationPlanId(UUID immunizationPlanId);

    @EntityGraph(attributePaths = {"user", "vaccineType", "immunizationPlan"})
    List<ImmunizationRecord> findByUserIdAndVaccineTypeId(UUID userId, UUID vaccineTypeId);

    Void deleteByUserIdAndId(UUID userId, UUID id);

    @Override
    @EntityGraph(attributePaths = {"user", "vaccineType", "immunizationPlan"})
    List<ImmunizationRecord> findAll();

    @Override
    @EntityGraph(attributePaths = {"user", "vaccineType", "immunizationPlan"})
    Optional<ImmunizationRecord> findById(UUID id);

    boolean existsByUserIdAndId(UUID userId, UUID id);
}
