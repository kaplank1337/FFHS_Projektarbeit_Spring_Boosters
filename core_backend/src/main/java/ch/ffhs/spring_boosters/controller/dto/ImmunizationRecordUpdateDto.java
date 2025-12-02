package ch.ffhs.spring_boosters.controller.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ImmunizationRecordUpdateDto(
        @NotNull(message = "ID is required")
        UUID id,

        @NotNull(message = "Vaccine type ID is required")
        UUID vaccineTypeId,

        @NotNull(message = "Administration date is required")
        LocalDate administeredOn,

        @NotNull(message = "Dose order claimed is required")
        Integer doseOrderClaimed
) {}
