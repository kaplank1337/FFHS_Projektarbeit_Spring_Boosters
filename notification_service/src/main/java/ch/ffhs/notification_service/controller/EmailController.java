package ch.ffhs.notification_service.controller;

import ch.ffhs.notification_service.controller.dto.EmailRequestDto;
import ch.ffhs.notification_service.controller.dto.EmailResponseDto;
import ch.ffhs.notification_service.service.EmailService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));
        body.put("message", "Validation failed");
        body.put("errors", fieldErrors);
        body.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        Map<String, String> violations = new HashMap<>();
        ex.getConstraintViolations().forEach(v -> violations.put(v.getPropertyPath().toString(), v.getMessage()));
        body.put("message", "Constraint violations");
        body.put("errors", violations);
        body.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Internal server error");
        body.put("error", ex.getMessage());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
