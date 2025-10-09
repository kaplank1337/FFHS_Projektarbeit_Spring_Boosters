package ch.ffhs.notification_service.service;

import ch.ffhs.notification_service.dto.EmailRequestDto;
import ch.ffhs.notification_service.dto.EmailResponseDto;
import ch.ffhs.notification_service.entity.EmailLog;
import ch.ffhs.notification_service.repository.EmailRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final EmailRepository emailRepository;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, SpringTemplateEngine templateEngine, EmailRepository emailRepository) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.emailRepository = emailRepository;
    }

    @Override
    public EmailResponseDto sendVaccinationEmail(EmailRequestDto emailRequest) {
        try {
            // Create Thymeleaf context with data
            Context context = new Context();
            context.setVariable("recipientName", emailRequest.recipientName());
            context.setVariable("vaccinations", emailRequest.vaccinations());

            // Process the HTML template
            String htmlContent = templateEngine.process("vaccination-email", context);

            // Create and send email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(emailRequest.recipientEmail());
            helper.setSubject(emailRequest.subject());
            helper.setText(htmlContent, true);
            helper.setFrom("noreply@springboosters.ch");

            mailSender.send(message);

            // Log successful email
            EmailLog log = new EmailLog(
                    emailRequest.recipientEmail(),
                    emailRequest.recipientName(),
                    emailRequest.subject(),
                    htmlContent,
                    true,
                    null
            );
            emailRepository.save(log);

            return new EmailResponseDto(
                    true,
                    "E-Mail erfolgreich gesendet an " + emailRequest.recipientEmail(),
                    LocalDateTime.now().toString()
            );

        } catch (MessagingException e) {
            // Log failed email
            EmailLog log = new EmailLog(
                    emailRequest.recipientEmail(),
                    emailRequest.recipientName(),
                    emailRequest.subject(),
                    null,
                    false,
                    e.getMessage()
            );
            emailRepository.save(log);

            return new EmailResponseDto(
                    false,
                    "Fehler beim Senden der E-Mail: " + e.getMessage(),
                    LocalDateTime.now().toString()
            );
        }
    }
}
