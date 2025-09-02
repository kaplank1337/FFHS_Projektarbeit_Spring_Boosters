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
@RequestMapping("/api/auth")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) throws UserAlreadyExistException {
        User user = userMapper.userDtoToUser(registrationDto);
        User registeredUser = userService.registerUser(user);
        UserDto userDto = userMapper.userToDto(registeredUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(@Valid @RequestBody UserLoginDto loginDto) {
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
