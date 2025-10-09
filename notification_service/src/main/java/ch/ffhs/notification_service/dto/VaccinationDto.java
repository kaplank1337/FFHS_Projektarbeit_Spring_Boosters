package ch.ffhs.notification_service.dto;

public record VaccinationDto(
        String vaccineName,
        String dueDate,
        String status,
        String description
) {
}
