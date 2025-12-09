package ch.ffhs.spring_boosters.controller.dto;

public record NotificationVaccinationDto(
        String vaccineName,
        String dueDate,
        String status,
        String description
) {
}
