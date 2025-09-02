package ch.ffhs.spring_boosters.controller.dto;

import ch.ffhs.spring_boosters.controller.entity.VaccineTypeActiveSubstance;

import java.util.List;

public record VaccineTypeDto(
        String name,
        String code,
        List<VaccineTypeActiveSubstance> vaccineTypeActiveSubstances
) {
}
