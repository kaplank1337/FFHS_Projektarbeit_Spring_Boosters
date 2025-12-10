package ch.ffhs.spring_boosters.controller.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ImmunizationRecordUpdateDto(
        @NotNull(message = "Administration date is required")
        LocalDate administeredOn,

        @NotNull(message = "Dose order claimed is required")
        Integer doseOrderClaimed
) {}
