package ch.ffhs.spring_boosters.controller.dto;

import ch.ffhs.spring_boosters.controller.entity.VaccineTypeActiveSubstance;

import java.util.List;
import java.util.UUID;

public record VaccineTypeDto(
        UUID id,
        String name,
        String code,
        List<VaccineTypeActiveSubstance> vaccineTypeActiveSubstances
) {
}
