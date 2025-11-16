package ch.ffhs.spring_boosters.controller.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ImmunizationRecordUpdateDto(
    @NotNull(message = "User ID is required")
    UUID userId,
    @NotNull(message = "Vaccine type ID is required")
    UUID vaccineTypeId,
    @NotNull(message = "Immunization plan ID is required")
    UUID immunizationPlanId,
    @NotNull(message = "Administration date is required")
    LocalDate administeredOn,
    Integer doseOrderClaimed
) {}
