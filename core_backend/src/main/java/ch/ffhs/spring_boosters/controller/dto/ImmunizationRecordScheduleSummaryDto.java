package ch.ffhs.spring_boosters.controller.dto;

public record ImmunizationRecordScheduleSummaryDto(
        String username,
        int totalPending,
        int highPriority,
        int mediumPriority,
        int lowPriority,
        int currentAgeDays
) {
}