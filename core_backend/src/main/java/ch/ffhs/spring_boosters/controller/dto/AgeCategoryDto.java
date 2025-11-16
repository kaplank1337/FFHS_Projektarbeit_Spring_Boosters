package ch.ffhs.spring_boosters.controller.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AgeCategoryDto(
    UUID id,
    String name,
    Integer ageMinDays,
    Integer ageMaxDays,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
