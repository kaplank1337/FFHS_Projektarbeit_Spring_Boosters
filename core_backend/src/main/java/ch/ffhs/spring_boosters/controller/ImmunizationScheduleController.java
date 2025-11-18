package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.config.JwtTokenReader;
import ch.ffhs.spring_boosters.controller.dto.ExceptionMessageBodyDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordScheduleSummaryDto;
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
public class ImmunizationScheduleController {

    private final ImmunizationScheduleService immunizationScheduleService;
    private final UserService userService;
    private final JwtTokenReader jwtTokenReader;

    @GetMapping("/pending")
    public ResponseEntity<ImmunizationScheduleDto> getOwnPendingImmunizations(@RequestHeader("Authorization") String authToken) throws UserNotFoundException {
        String username = extractUsernameFromJwt(authToken);

        UUID userId = userService.findByUsername(username).getId();
        ImmunizationScheduleDto schedule = immunizationScheduleService.getPendingImmunizations(userId);
        return ResponseEntity.ok(schedule);

    }

    @GetMapping("/pending/summary")
    public ResponseEntity<ImmunizationRecordScheduleSummaryDto> getOwnPendingImmunizationsSummary(@RequestHeader("Authorization") String authToken) throws UserNotFoundException {
        String username = extractUsernameFromJwt(authToken);

            UUID userId = userService.findByUsername(username).getId();
            ImmunizationScheduleDto schedule = immunizationScheduleService.getPendingImmunizations(userId);
            ImmunizationRecordScheduleSummaryDto summary = new ImmunizationRecordScheduleSummaryDto(
                schedule.getUsername(),
                schedule.getTotalPending(),
                schedule.getHighPriority(),
                schedule.getMediumPriority(),
                schedule.getLowPriority(),
                schedule.getCurrentAgeDays()
            );
            return ResponseEntity.ok(summary);
    }

    private String extractUsernameFromJwt(String authToken) {
        String token = authToken.replace("Bearer ", "");
        return jwtTokenReader.getUsername(token);
    }
}
