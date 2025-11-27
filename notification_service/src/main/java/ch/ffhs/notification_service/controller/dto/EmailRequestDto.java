package ch.ffhs.notification_service.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record EmailRequestDto(
        @NotBlank(message = "recipientEmail darf nicht leer sein")
        @Email(message = "recipientEmail muss eine gültige E-Mail-Adresse sein")
        String recipientEmail,
        @NotBlank(message = "recipientName darf nicht leer sein")
        @Size(max = 255, message = "recipientName darf maximal 255 Zeichen lang sein")
        String recipientName,
        @NotBlank(message = "subject darf nicht leer sein")
        @Size(max = 255, message = "subject darf maximal 255 Zeichen lang sein")
        String subject,
        @NotEmpty(message = "Es muss mindestens eine Impfung angegeben werden")
        List<VaccinationDto> vaccinations
) {
}
