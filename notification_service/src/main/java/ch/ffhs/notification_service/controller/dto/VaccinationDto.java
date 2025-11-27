package ch.ffhs.notification_service.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VaccinationDto(
        @NotBlank(message = "vaccineName darf nicht leer sein")
        @Size(max = 255, message = "vaccineName darf maximal 255 Zeichen lang sein")
        String vaccineName,
        @NotBlank(message = "dueDate darf nicht leer sein")
        String dueDate,
        @NotBlank(message = "status darf nicht leer sein")
        @Size(max = 50, message = "status darf maximal 50 Zeichen lang sein")
        String status,
        @Size(max = 500, message = "description darf maximal 500 Zeichen lang sein")
        String description
) {
}
