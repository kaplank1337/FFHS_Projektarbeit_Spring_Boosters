package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.config.JwtTokenReader;
import ch.ffhs.spring_boosters.controller.entity.User;
import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;
import ch.ffhs.spring_boosters.service.ReminderService;
import ch.ffhs.spring_boosters.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reminderservice")
@RequiredArgsConstructor
public class demoController {

    private final ReminderService reminderService;
    private final UserService userService;
    private final JwtTokenReader jwtTokenReader;

    @GetMapping("/execute")
    public ResponseEntity<String> demo(@RequestHeader("Authorization") String authToken) throws UserNotFoundException {
            User user = userService.findById(getUserIdFromToken(authToken));
            reminderService.executeReminder(user);

        return ResponseEntity.ok("Reminder service executed");
    }

    private UUID getUserIdFromToken(String authToken) {
        String token = authToken.replace("Bearer ", "");
        return UUID.fromString(jwtTokenReader.getUserId(token));
    }
}
