package ch.ffhs.notification_service.service;

import ch.ffhs.notification_service.controller.dto.EmailRequestDto;
import ch.ffhs.notification_service.controller.dto.VaccinationDto;
import ch.ffhs.notification_service.entity.EmailLog;
import ch.ffhs.notification_service.repository.EmailRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    private JavaMailSender mailSender;
    private SpringTemplateEngine templateEngine;
    private EmailRepository emailRepository;
    private EmailServiceImpl service;

    @BeforeEach
    void setUp() {
        // Mockito mock for JavaMailSender
        mailSender = mock(org.springframework.mail.javamail.JavaMailSender.class);
        templateEngine = mock(SpringTemplateEngine.class);
        emailRepository = mock(EmailRepository.class);

        service = new EmailServiceImpl(mailSender, templateEngine, emailRepository);
        ReflectionTestUtils.setField(service, "fromAddress", "noreply@example.com");
        ReflectionTestUtils.setField(service, "fromPersonal", "No Reply");
    }

    @Test
    void sendVaccinationEmail_success_savesLogAndReturnsSuccess() {
        var vacc = new VaccinationDto("COVID", "2025-12-01", "PENDING", "desc");
        EmailRequestDto req = new EmailRequestDto("to@example.com", "Recipient", "Subject", List.of(vacc));

        when(templateEngine.process(eq("vaccination-email"), any())).thenReturn("<html>ok</html>");
        MimeMessage msg = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(msg);
        // mailSender.send does nothing
        doNothing().when(mailSender).send(msg);
        when(emailRepository.save(any(EmailLog.class))).thenAnswer(i -> i.getArgument(0));

        var resp = service.sendVaccinationEmail(req);

        assertTrue(resp.success());
        assertTrue(resp.message().contains(req.recipientEmail()));
        assertNotNull(resp.timestamp());

        ArgumentCaptor<EmailLog> cap = ArgumentCaptor.forClass(EmailLog.class);
        verify(emailRepository, times(1)).save(cap.capture());
        EmailLog log = cap.getValue();
        assertEquals(req.recipientEmail(), log.getRecipientEmail());
        assertEquals(req.recipientName(), log.getRecipientName());
        assertEquals("Subject", log.getSubject());
        assertTrue(log.isSuccess());
        assertNotNull(log.getContent());
        assertNull(log.getErrorMessage());
    }

    @Test
    void sendVaccinationEmail_emptySubject_usesDefaultSubject() {
        var vacc = new VaccinationDto("COVID", "2025-12-01", "PENDING", "desc");
        EmailRequestDto req = new EmailRequestDto("to@example.com", "Recipient", "", List.of(vacc));

        when(templateEngine.process(eq("vaccination-email"), any())).thenReturn("<html>ok</html>");
        MimeMessage msg = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(msg);
        doNothing().when(mailSender).send(msg);
        when(emailRepository.save(any(EmailLog.class))).thenAnswer(i -> i.getArgument(0));

        var resp = service.sendVaccinationEmail(req);

        assertTrue(resp.success());
        ArgumentCaptor<EmailLog> cap = ArgumentCaptor.forClass(EmailLog.class);
        verify(emailRepository).save(cap.capture());
        EmailLog log = cap.getValue();
        assertEquals("Impfbenachrichtigung", log.getSubject());
    }

    @Test
    void sendVaccinationEmail_whenMailSendThrows_logsFailureAndReturnsFalse() {
        var vacc = new VaccinationDto("COVID", "2025-12-01", "PENDING", "desc");
        EmailRequestDto req = new EmailRequestDto("to@example.com", "Recipient", "Subject", List.of(vacc));

        when(templateEngine.process(eq("vaccination-email"), any())).thenReturn("<html>ok</html>");
        MimeMessage msg = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(msg);
        doThrow(new org.springframework.mail.MailSendException("send-fail")).when(mailSender).send(msg);
        when(emailRepository.save(any(EmailLog.class))).thenAnswer(i -> i.getArgument(0));

        var resp = service.sendVaccinationEmail(req);

        assertFalse(resp.success());
        assertTrue(resp.message().contains("send-fail"));

        ArgumentCaptor<EmailLog> cap = ArgumentCaptor.forClass(EmailLog.class);
        verify(emailRepository).save(cap.capture());
        EmailLog log = cap.getValue();
        assertFalse(log.isSuccess());
        assertNotNull(log.getErrorMessage());
        assertTrue(log.getErrorMessage().contains("send-fail"));
    }
}
