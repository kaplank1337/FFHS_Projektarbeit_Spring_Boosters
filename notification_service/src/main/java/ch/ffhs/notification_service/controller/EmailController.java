package ch.ffhs.notification_service.controller;

import ch.ffhs.notification_service.dto.EmailRequestDto;
import ch.ffhs.notification_service.dto.EmailResponseDto;
import ch.ffhs.notification_service.service.EmailService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<EmailResponseDto> sendEmail(@Valid @RequestBody EmailRequestDto emailRequest) {
        EmailResponseDto response = emailService.sendVaccinationEmail(emailRequest);

        if (response.success()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(500).body(response);
        }
    }
}
