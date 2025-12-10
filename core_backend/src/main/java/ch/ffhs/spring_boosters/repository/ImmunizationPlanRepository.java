package ch.ffhs.spring_boosters.repository;

import ch.ffhs.spring_boosters.controller.entity.ImmunizationPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImmunizationPlanRepository extends JpaRepository<ImmunizationPlan, UUID> {
    Optional<ImmunizationPlan> findByName(String name);
    boolean existsByName(String name);

    List<ImmunizationPlan> findByVaccineTypeIdAndAgeCategoryId(UUID vaccineTypeId, UUID ageCategory);
    List<ImmunizationPlan> findByAgeCategoryId(UUID ageCategoryId);
    List<ImmunizationPlan> findByVaccineTypeId(UUID vaccineTypeId);
}
