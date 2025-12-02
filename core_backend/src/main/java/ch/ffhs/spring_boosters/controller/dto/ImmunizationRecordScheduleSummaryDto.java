package ch.ffhs.spring_boosters.controller.dto;

public record ImmunizationRecordScheduleSummaryDto(
        int totalPending,
        int overdueCount,
        int dueSoonCount,
        int upcomingDueCount,
        int currentAgeDays
) {
}