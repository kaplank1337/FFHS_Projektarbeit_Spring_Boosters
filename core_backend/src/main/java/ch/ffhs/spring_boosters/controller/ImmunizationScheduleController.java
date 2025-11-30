package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.config.JwtTokenReader;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordScheduleSummaryDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationScheduleDto;
import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;
import ch.ffhs.spring_boosters.service.ImmunizationScheduleService;
import ch.ffhs.spring_boosters.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/immunization-schedule")
@AllArgsConstructor
public class ImmunizationScheduleController {

    private final ImmunizationScheduleService immunizationScheduleService;
    private final UserService userService;
    private final JwtTokenReader jwtTokenReader;

    //TODO: Kaan --> Dieser Endpunkt sollte den vollen Plan zurückliefern, welche Imfpungen ausstehen, inklusive Details.
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
                schedule.getTotalPending(),
                schedule.getOverdueCount(),
                schedule.getDueSoonCount(),
                schedule.getUpcomingDueCount(),
                schedule.getCurrentAgeDays()
            );
            return ResponseEntity.ok(summary);
    }

    private String extractUsernameFromJwt(String authToken) {
        String token = authToken.replace("Bearer ", "");
        return jwtTokenReader.getUsername(token);
    }
}
