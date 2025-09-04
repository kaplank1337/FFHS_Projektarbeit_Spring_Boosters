package ch.ffhs.spring_boosters.controller.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ImmunizationRecordDto(
    UUID id,
    UUID userId,
    UUID vaccineTypeId,
    UUID immunizationPlanId,
    LocalDate administeredOn,
    Integer doseOrderClaimed,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
