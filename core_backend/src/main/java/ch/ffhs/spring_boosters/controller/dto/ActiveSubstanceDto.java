package ch.ffhs.spring_boosters.controller.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ActiveSubstanceDto(
    UUID id,
    String name,
    String[] synonyms,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
