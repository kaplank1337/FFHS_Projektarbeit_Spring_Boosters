package ch.ffhs.spring_boosters.controller.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ImmunizationRecordDto(
    UUID id,
    LocalDate administeredOn,
    Integer doseOrderClaimed,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
