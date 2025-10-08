package ch.ffhs.spring_boosters.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ImmunizationPlanUpdateDto(
    @NotBlank(message = "Name is required")
    String name,
    @NotNull(message = "Vaccine type ID is required")
    UUID vaccineTypeId,
    @NotNull(message = "Age category ID is required")
    UUID ageCategoryId
) {}
