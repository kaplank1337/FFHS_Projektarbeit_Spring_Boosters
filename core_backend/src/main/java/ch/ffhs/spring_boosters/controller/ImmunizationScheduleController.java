package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.controller.dto.ExceptionMessageBodyDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationScheduleDto;
import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;
import ch.ffhs.spring_boosters.service.ImmunizationScheduleService;
import ch.ffhs.spring_boosters.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/immunization-schedule")
@AllArgsConstructor
@Tag(name = "Impfplan", description = "API-Endpoints für die Abfrage von ausstehenden eigenen Impfungen")
public class ImmunizationScheduleController {

    private final ImmunizationScheduleService immunizationScheduleService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @GetMapping("/pending")
    @Operation(summary = "Eigene ausstehenden Impfungen abrufen", description = "Verwendet den JWT Subject (sub) aus dem Authorization Header.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ausstehende Impfungen erfolgreich abgerufen", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImmunizationScheduleDto.class))),
            @ApiResponse(responseCode = "401", description = "Nicht authentifiziert"),
            @ApiResponse(responseCode = "404", description = "Benutzer nicht gefunden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionMessageBodyDto.class)))
    })
    public ResponseEntity<?> getOwnPendingImmunizations(@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
                                                        HttpServletRequest request) {
        String username = extractUsernameFromJwt(authorizationHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ExceptionMessageBodyDto(
                    LocalDateTime.now(),
                    HttpStatus.UNAUTHORIZED.value(),
                    "Unauthorized",
                    "Kein gültiger Bearer Token oder 'sub' Claim fehlt.",
                    request.getRequestURI()
            ));
        }
        try {
            UUID userId = userService.findByUsername(username).getId();
            ImmunizationScheduleDto schedule = immunizationScheduleService.getPendingImmunizations(userId);
            return ResponseEntity.ok(schedule);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionMessageBodyDto(
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    "Benutzer nicht gefunden: " + username,
                    request.getRequestURI()
            ));
        }
    }

    @GetMapping("/pending/summary")
    @Operation(summary = "Zusammenfassung eigener ausstehender Impfungen", description = "Kurzübersicht für authentifizierten Benutzer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Zusammenfassung erfolgreich abgerufen"),
            @ApiResponse(responseCode = "401", description = "Nicht authentifiziert"),
            @ApiResponse(responseCode = "404", description = "Benutzer nicht gefunden")
    })
    public ResponseEntity<?> getOwnPendingImmunizationsSummary(@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
                                                               HttpServletRequest request) {
        String username = extractUsernameFromJwt(authorizationHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ExceptionMessageBodyDto(
                    LocalDateTime.now(),
                    HttpStatus.UNAUTHORIZED.value(),
                    "Unauthorized",
                    "Kein gültiger Bearer Token oder 'sub' Claim fehlt.",
                    request.getRequestURI()
            ));
        }
        try {
            UUID userId = userService.findByUsername(username).getId();
            ImmunizationScheduleDto schedule = immunizationScheduleService.getPendingImmunizations(userId);
            java.util.Map<String, Object> summary = new java.util.LinkedHashMap<>();
            summary.put("userId", schedule.getUserId());
            summary.put("username", schedule.getUsername());
            summary.put("totalPending", schedule.getTotalPending());
            summary.put("highPriority", schedule.getHighPriority());
            summary.put("mediumPriority", schedule.getMediumPriority());
            summary.put("lowPriority", schedule.getLowPriority());
            summary.put("currentAgeDays", schedule.getCurrentAgeDays());
            return ResponseEntity.ok(summary);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionMessageBodyDto(
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    "Benutzer nicht gefunden: " + username,
                    request.getRequestURI()
            ));
        }
    }

    private String extractUsernameFromJwt(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authorizationHeader.substring(7);
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            return null;
        }
        try {
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            JsonNode node = objectMapper.readTree(payloadJson);
            return node.has("sub") ? node.get("sub").asText() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
