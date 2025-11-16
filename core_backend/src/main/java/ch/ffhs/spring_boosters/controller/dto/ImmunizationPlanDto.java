package ch.ffhs.spring_boosters.controller.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ImmunizationPlanDto(
    UUID id,
    String name,
    UUID vaccineTypeId,
    UUID ageCategoryId,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
