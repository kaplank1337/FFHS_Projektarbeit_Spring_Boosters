package ch.ffhs.notification_service.service;

import ch.ffhs.notification_service.controller.dto.EmailRequestDto;
import ch.ffhs.notification_service.controller.dto.EmailResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    EmailResponseDto sendVaccinationEmail(EmailRequestDto emailRequest);
}
