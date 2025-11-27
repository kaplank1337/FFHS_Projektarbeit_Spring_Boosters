package ch.ffhs.notification_service.controller;

import ch.ffhs.notification_service.controller.dto.EmailRequestDto;
import ch.ffhs.notification_service.controller.dto.EmailResponseDto;
import ch.ffhs.notification_service.controller.dto.VaccinationDto;
import ch.ffhs.notification_service.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmailControllerTest {

    private MockMvc mockMvc;
    private EmailService emailService;
    private ObjectMapper mapper;
    private EmailController controller;

    @BeforeEach
    void setUp() {
        emailService = Mockito.mock(EmailService.class);
        controller = new EmailController(emailService);

        // enable validation for @Valid on controller
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .build();

        mapper = new ObjectMapper();
    }

    @Test
    void sendEmail_success_returns200AndBody() throws Exception {
        EmailResponseDto responseDto = new EmailResponseDto(true, "ok", "2025-11-27T00:00:00Z");
        when(emailService.sendVaccinationEmail(any(EmailRequestDto.class))).thenReturn(responseDto);

        var vaccination = new VaccinationDto("COVID-19", "2025-12-01", "PENDING", "desc");
        EmailRequestDto req = new EmailRequestDto("to@example.com", "Recipient", "Subject", java.util.List.of(vaccination));

        var mvcResult = mockMvc.perform(post("/api/v1/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        String respJson = mvcResult.getResponse().getContentAsString();
        EmailResponseDto resp = mapper.readValue(respJson, EmailResponseDto.class);

        assertTrue(resp.success());
        assertEquals("ok", resp.message());
        assertNotNull(resp.timestamp());
        verify(emailService, times(1)).sendVaccinationEmail(any(EmailRequestDto.class));
    }

    @Test
    void sendEmail_failure_returns500AndBody() throws Exception {
        EmailResponseDto responseDto = new EmailResponseDto(false, "failed", null);
        when(emailService.sendVaccinationEmail(any(EmailRequestDto.class))).thenReturn(responseDto);

        var vaccination = new VaccinationDto("COVID-19", "2025-12-01", "PENDING", "desc");
        EmailRequestDto req = new EmailRequestDto("to@example.com", "Recipient", "Subject", java.util.List.of(vaccination));

        var mvcResult = mockMvc.perform(post("/api/v1/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String respJson = mvcResult.getResponse().getContentAsString();
        EmailResponseDto resp = mapper.readValue(respJson, EmailResponseDto.class);

        assertFalse(resp.success());
        assertEquals("failed", resp.message());
        assertNull(resp.timestamp());
        verify(emailService, times(1)).sendVaccinationEmail(any(EmailRequestDto.class));
    }

    @Test
    void sendEmail_invalidRequest_returns400() throws Exception {
        // send empty JSON -> should fail @Valid constraints
        String emptyJson = "{}";

        mockMvc.perform(post("/api/v1/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyJson))
                .andExpect(status().isBadRequest());

        verify(emailService, never()).sendVaccinationEmail(any());
    }

    @Test
    void handleMethodArgumentNotValid_returnsStructuredError() throws Exception {
        // prepare a binding result with a field error
        var vaccination = new VaccinationDto("COVID-19", "2025-12-01", "PENDING", "desc");
        EmailRequestDto target = new EmailRequestDto("", "", "", java.util.List.of(vaccination));
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "emailRequest");
        bindingResult.addError(new FieldError("emailRequest", "recipientEmail", "recipientEmail darf nicht leer sein"));

        Method method = EmailController.class.getMethod("sendEmail", EmailRequestDto.class);
        MethodParameter param = new MethodParameter(method, 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(param, bindingResult);

        var response = controller.handleMethodArgumentNotValid(ex);
        assertEquals(400, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Validation failed", body.get("message"));
        Map<?, ?> errors = (Map<?, ?>) body.get("errors");
        assertEquals("recipientEmail darf nicht leer sein", errors.get("recipientEmail"));
        assertEquals(400, body.get("status"));
    }

    @Test
    void handleConstraintViolation_returnsStructuredError() {
        // mock a ConstraintViolation
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> cv = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("vaccinations[0].dueDate");
        when(cv.getPropertyPath()).thenReturn(path);
        when(cv.getMessage()).thenReturn("dueDate must not be null");

        ConstraintViolationException ex = new ConstraintViolationException("violations", Set.of(cv));

        var response = controller.handleConstraintViolation(ex);
        assertEquals(400, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Constraint violations", body.get("message"));
        Map<?, ?> errors = (Map<?, ?>) body.get("errors");
        assertEquals("dueDate must not be null", errors.get("vaccinations[0].dueDate"));
        assertEquals(400, body.get("status"));
    }

    @Test
    void handleIllegalArgument_returnsBadRequest() {
        var response = controller.handleIllegalArgument(new IllegalArgumentException("bad arg"));
        assertEquals(400, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("bad arg", body.get("message"));
        assertEquals(400, body.get("status"));
    }

    @Test
    void handleGeneric_returnsInternalServerError() {
        var response = controller.handleGeneric(new Exception("boom"));
        assertEquals(500, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Internal server error", body.get("message"));
        assertEquals("boom", body.get("error"));
        assertEquals(500, body.get("status"));
    }
}
