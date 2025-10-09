package ch.ffhs.notification_service.controller;

import ch.ffhs.notification_service.dto.EmailRequestDto;
import ch.ffhs.notification_service.dto.EmailResponseDto;
import ch.ffhs.notification_service.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<EmailResponseDto> sendEmail(@RequestBody EmailRequestDto emailRequest) {
        EmailResponseDto response = emailService.sendVaccinationEmail(emailRequest);

        if (response.success()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(500).body(response);
        }
    }
}
