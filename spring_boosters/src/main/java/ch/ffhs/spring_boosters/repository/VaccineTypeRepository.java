package ch.ffhs.spring_boosters.repository;

import ch.ffhs.spring_boosters.controller.entity.VaccineType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VaccineTypeRepository extends JpaRepository<VaccineType, UUID> {
}
