package ch.ffhs.spring_boosters.controller.dto;

import java.util.List;

public record NotificationEmailRequestDto(
        String recipientEmail,
        String recipientName,
        String subject,
        List<NotificationVaccinationDto>vaccinations
) {
}