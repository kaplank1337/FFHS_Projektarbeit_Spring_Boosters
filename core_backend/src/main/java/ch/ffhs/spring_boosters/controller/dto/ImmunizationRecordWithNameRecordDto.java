package ch.ffhs.spring_boosters.controller.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ImmunizationRecordWithNameRecordDto(
        UUID id,
        LocalDate administeredOn,
        String vaccineName,
        Integer doseOrderClaimed,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
