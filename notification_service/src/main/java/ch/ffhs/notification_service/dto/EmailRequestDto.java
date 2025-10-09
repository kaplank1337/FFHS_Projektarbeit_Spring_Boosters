package ch.ffhs.notification_service.dto;

import java.util.List;

public record EmailRequestDto(
        String recipientEmail,
        String recipientName,
        String subject,
        List<VaccinationDto> vaccinations
) {
}

