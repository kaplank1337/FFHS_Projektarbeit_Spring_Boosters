package ch.ffhs.spring_boosters.repository;

import ch.ffhs.spring_boosters.controller.entity.ActiveSubstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ActiveSubstanceRepository extends JpaRepository<ActiveSubstance, UUID> {
    boolean existsByName(String name);
}
