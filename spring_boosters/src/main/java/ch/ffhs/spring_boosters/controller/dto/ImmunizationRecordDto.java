package ch.ffhs.spring_boosters.controller.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ImmunizationRecordDto(
    UUID id,
    UserDto user,
    VaccineTypeDto vaccineType,
    ImmunizationPlanDto immunizationPlan,
    LocalDate administeredOn,
    Integer doseOrderClaimed,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
