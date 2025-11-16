package ch.ffhs.spring_boosters.repository;

import ch.ffhs.spring_boosters.controller.entity.AgeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AgeCategoryRepository extends JpaRepository<AgeCategory, UUID> {
    Optional<AgeCategory> findByName(String name);
    boolean existsByName(String name);
}
