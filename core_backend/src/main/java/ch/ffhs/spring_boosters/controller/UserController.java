package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.controller.dto.LoginResponseDto;
import ch.ffhs.spring_boosters.controller.dto.ExceptionMessageBodyDto;
import ch.ffhs.spring_boosters.controller.mapper.UserMapper;
import ch.ffhs.spring_boosters.controller.dto.UserDto;
import ch.ffhs.spring_boosters.controller.dto.UserLoginDto;
import ch.ffhs.spring_boosters.controller.dto.UserRegistrationDto;
import ch.ffhs.spring_boosters.controller.entity.User;
import ch.ffhs.spring_boosters.service.Exception.UserAlreadyExistException;
import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;
import ch.ffhs.spring_boosters.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@Tag(name = "Authentifizierung", description = "API-Endpoints für Benutzerregistrierung, Login und Benutzerverwaltung")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(
        @Valid @RequestBody UserRegistrationDto registrationDto) throws UserAlreadyExistException {
        User user = userMapper.userDtoToUser(registrationDto);
        User registeredUser = userService.registerUser(user);
        UserDto userDto = userMapper.userToDto(registeredUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(
        @Valid @RequestBody UserLoginDto loginDto) {
        try {
            // Authentifizierung erfolgt über den Gateway Service
            // Hier wird nur geprüft, ob der Benutzer existiert
            User user = userService.findByUsername(loginDto.getUsername());
            UserDto userDto = userMapper.userToDto(user);

            return ResponseEntity.ok(new LoginResponseDto(
                    true,
                    "User found - authentication handled by gateway",
                    loginDto.getUsername(),
                    null, // Token wird vom Gateway generiert
                    userDto
            ));

        } catch (UserNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponseDto(
                            false,
                            "User not found",
                            null,
                            null,
                            null
                    ));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            // User-ID wird vom Gateway im Header gesetzt
            if (userId == null || userId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            User user = userService.findById(java.util.UUID.fromString(userId));
            UserDto userDto = userMapper.userToDto(user);

            return ResponseEntity.ok(userDto);

        } catch (UserNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) throws UserNotFoundException {
        try {
            userService.deleteUser(java.util.UUID.fromString(userId));
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    @ExceptionHandler({UserAlreadyExistException.class, MethodArgumentNotValidException.class , ValidationException.class})
    public ResponseEntity<ExceptionMessageBodyDto> handleUserAlreadyExistException(
            Exception ex,
            HttpServletRequest request) {

        ExceptionMessageBodyDto errorResponse = new ExceptionMessageBodyDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI(),
                ex.getClass().getSimpleName()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
