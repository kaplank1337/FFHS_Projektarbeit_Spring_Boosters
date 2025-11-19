package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.config.JwtTokenReader;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@Tag(name = "Authentifizierung", description = "API-Endpoints für Benutzerregistrierung, Login und Benutzerverwaltung")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtTokenReader jwtTokenReader;

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
            User user = userService.findByUsernameAndPassword(loginDto.getUsername(), loginDto.getPassword());

            // JWT-Token im Core Backend generieren
            String token = userService.generateToken(user);

            UserDto userDto = userMapper.userToDto(user);

            return ResponseEntity.ok(new LoginResponseDto(
                    true,
                    "Login successful",
                    loginDto.getUsername(),
                    token,
                    userDto
            ));

        } catch (UserNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponseDto(
                            false,
                            "Invalid credentials",
                            null,
                            null,
                            null
                    ));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser( @RequestHeader("Authorization") String authToken) {
        try {
            User user = userService.findById(getUserIdFromToken(authToken));
            UserDto userDto = userMapper.userToDto(user);

            return ResponseEntity.ok(userDto);

        } catch (UserNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteUser( @RequestHeader("Authorization") String authToken) throws UserNotFoundException {
        try {
            String token = authToken.replace("Bearer ", "");
            UUID userId = UUID.fromString(jwtTokenReader.getUserId(token));

            userService.deleteUser(userId);
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

    private UUID getUserIdFromToken(String authToken) {
        String token = authToken.replace("Bearer ", "");
        return UUID.fromString(jwtTokenReader.getUserId(token));
    }
}
