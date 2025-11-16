package ch.ffhs.spring_boosters.controller.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FollowUpRuleDto(
    UUID id,
    UUID fromPlanId,
    UUID toPlanId,
    UUID requiredSeriesId,
    Integer minCompletedDoses,
    Integer targetMinAgeDays,
    Integer targetMaxAgeDays,
    Integer minIntervalDaysSinceLast,
    Integer preferredAgeDays,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
