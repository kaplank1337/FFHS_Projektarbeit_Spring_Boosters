package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.config.JwtUtil;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@Tag(name = "Authentifizierung", description = "API-Endpoints für Benutzerregistrierung, Login und Benutzerverwaltung")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    @Operation(
        summary = "Benutzer registrieren",
        description = "Erstellt einen neuen Benutzer im System. Der Benutzername muss eindeutig sein."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Benutzer erfolgreich registriert",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDto.class),
                examples = @ExampleObject(
                    name = "Erfolgreiche Registrierung",
                    value = """
                    {
                        "id": "123e4567-e89b-12d3-a456-426614174000",
                        "username": "john.doe",
                        "firstName": "John",
                        "lastName": "Doe",
                        "birthDate": "1990-05-15",
                        "role": "USER"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Ungültige Eingabedaten oder Benutzer existiert bereits",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ExceptionMessageBodyDto.class)
            )
        )
    })
    public ResponseEntity<UserDto> registerUser(
        @Parameter(description = "Registrierungsdaten des neuen Benutzers", required = true)
        @Valid @RequestBody UserRegistrationDto registrationDto) throws UserAlreadyExistException {
        User user = userMapper.userDtoToUser(registrationDto);
        User registeredUser = userService.registerUser(user);
        UserDto userDto = userMapper.userToDto(registeredUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @PostMapping("/login")
    @Operation(
        summary = "Benutzer anmelden",
        description = "Authentifiziert einen Benutzer und gibt ein JWT-Token zurück"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login erfolgreich",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponseDto.class),
                examples = @ExampleObject(
                    name = "Erfolgreicher Login",
                    value = """
                    {
                        "success": true,
                        "message": "Login successful",
                        "username": "john.doe",
                        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                        "user": {
                            "id": "123e4567-e89b-12d3-a456-426614174000",
                            "username": "john.doe",
                            "firstName": "John",
                            "lastName": "Doe",
                            "birthDate": "1990-05-15",
                            "role": "USER"
                        }
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Ungültige Anmeldedaten",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponseDto.class)
            )
        )
    })
    public ResponseEntity<LoginResponseDto> loginUser(
        @Parameter(description = "Anmeldedaten des Benutzers", required = true)
        @Valid @RequestBody UserLoginDto loginDto) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userService.findByUsername(loginDto.getUsername());
            UserDto userDto = userMapper.userToDto(user);
            String jwtToken = jwtUtil.generateToken(userDto);

            return ResponseEntity.ok(new LoginResponseDto(
                    true,
                    "Login successful",
                    loginDto.getUsername(),
                    jwtToken,
                    userDto
            ));

        } catch (AuthenticationException exception){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponseDto(
                            false,
                            "Invalid credentials",
                            null,
                            null,
                            null
                    ));
        } catch (UserNotFoundException exception){
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
    @Operation(
        summary = "Aktueller Benutzer",
        description = "Gibt die Daten des aktuell angemeldeten Benutzers zurück",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Benutzerdaten erfolgreich abgerufen",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Nicht authentifiziert oder ungültiges Token"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Benutzer nicht gefunden"
        )
    })
    public ResponseEntity<UserDto> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            User user = userService.findByUsername(authentication.getName());
            UserDto userDto = userMapper.userToDto(user);

            return ResponseEntity.ok(userDto);

        } catch (UserNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping
    @Operation(
        summary = "Benutzer löschen",
        description = "Löscht den aktuell angemeldeten Benutzer aus dem System",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Benutzer erfolgreich gelöscht"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Nicht authentifiziert oder ungültiges Token"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Benutzer nicht gefunden"
        )
    })
    public ResponseEntity<Void> deleteCurrentUser() throws UserNotFoundException {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            userService.deleteUser(authentication.getName());
            return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({UserAlreadyExistException.class, MethodArgumentNotValidException.class , ValidationException.class, UserAlreadyExistException.class})
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
