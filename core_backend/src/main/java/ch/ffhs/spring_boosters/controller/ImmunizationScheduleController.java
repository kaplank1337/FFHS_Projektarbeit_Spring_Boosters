package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.config.JwtTokenReader;
import ch.ffhs.spring_boosters.controller.config.StringToPriorityRequestDtoConverter;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordScheduleSummaryDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationSchedulRecordSortedByPriorityDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationScheduleDto;
import ch.ffhs.spring_boosters.controller.dto.PriorityRequestDto;
import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;
import ch.ffhs.spring_boosters.service.ImmunizationRecordService;
import ch.ffhs.spring_boosters.service.ImmunizationScheduleService;
import ch.ffhs.spring_boosters.service.UserService;
import ch.ffhs.spring_boosters.service.implementation.enumerator.PriorityEnum;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/immunization-schedule")
@AllArgsConstructor
public class ImmunizationScheduleController {

    private final ImmunizationScheduleService immunizationScheduleService;
    private final UserService userService;
    private final StringToPriorityRequestDtoConverter stringToPriorityRequestDtoConverter;
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
                schedule.getTotalPending(),
                schedule.getOverdueCount(),
                schedule.getDueSoonCount(),
                schedule.getUpcomingDueCount(),
                schedule.getCurrentAgeDays()
            );
            return ResponseEntity.ok(summary);
    }

    @GetMapping("/pending/{priority}")
    public ResponseEntity<ImmunizationSchedulRecordSortedByPriorityDto> getOwnImmunizationSchedule(
            @RequestHeader("Authorization") String authToken,
            @PathVariable @Valid PriorityRequestDto priority, Sort sort) throws UserNotFoundException {

        UUID userId = extractUserIdFromJwt(authToken);
        PriorityEnum priorityEnum = priority.toEnum();

        ImmunizationSchedulRecordSortedByPriorityDto sortedSchedule =
                immunizationScheduleService.getImmunizationRecordsByUserIdAndFilterByDueStatus(userId, priorityEnum);

        return ResponseEntity.ok(sortedSchedule);
    }

    private String extractUsernameFromJwt(String authToken) {
        String token = authToken.replace("Bearer ", "");
        return jwtTokenReader.getUsername(token);
    }

    private UUID extractUserIdFromJwt(String authToken) {
        String token = authToken.replace("Bearer ", "");
        return UUID.fromString(jwtTokenReader.getUserId(token));
    }
}
