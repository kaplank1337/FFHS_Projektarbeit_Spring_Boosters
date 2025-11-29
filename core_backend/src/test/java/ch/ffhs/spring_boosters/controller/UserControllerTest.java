package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.config.JwtTokenReader;
import ch.ffhs.spring_boosters.controller.dto.UserDto;
import ch.ffhs.spring_boosters.controller.dto.UserLoginDto;
import ch.ffhs.spring_boosters.controller.dto.UserRegistrationDto;
import ch.ffhs.spring_boosters.controller.entity.User;
import ch.ffhs.spring_boosters.controller.exception.GlobalExceptionHandler;
import ch.ffhs.spring_boosters.controller.mapper.UserMapper;
import ch.ffhs.spring_boosters.service.Exception.UserAlreadyExistException;
import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;
import ch.ffhs.spring_boosters.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtTokenReader jwtTokenReader;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        UserController controller = new UserController(userService, userMapper, jwtTokenReader);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private User sampleUser(UUID id, String username) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        u.setPasswordHash("hashed");
        u.setFirstName("First");
        u.setLastName("Last");
        u.setBirthDate(LocalDate.of(1990,1,1));
        return u;
    }

    private UserDto sampleUserDto(UUID id, String username) {
        return new UserDto(id, username, "First", "Last", LocalDate.of(1990,1,1), "USER");
    }

    @Test
    void registerUser_success() throws Exception {
        UUID id = UUID.randomUUID();
        UserRegistrationDto req = new UserRegistrationDto("alice", "Alice", "Doe", LocalDate.of(1990,1,1), "secret123");
        User entityFromDto = sampleUser(null, "alice");
        User created = sampleUser(id, "alice");
        UserDto responseDto = sampleUserDto(id, "alice");

        when(userMapper.userDtoToUser(any(UserRegistrationDto.class))).thenReturn(entityFromDto);
        when(userService.registerUser(entityFromDto)).thenReturn(created);
        when(userMapper.userToDto(created)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.username", is("alice")));
    }

    @Test
    void registerUser_alreadyExists_returnsBadRequest() throws Exception {
        UserRegistrationDto req = new UserRegistrationDto("bob", "Bob", "Smith", LocalDate.of(1985,5,5), "pw12345");
        User entityFromDto = sampleUser(null, "bob");

        when(userMapper.userDtoToUser(any(UserRegistrationDto.class))).thenReturn(entityFromDto);
        when(userService.registerUser(entityFromDto)).thenThrow(new UserAlreadyExistException("Already exists"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")));
    }

    @Test
    void registerUser_validationFails_missingFields() throws Exception {
        // missing username and password
        UserRegistrationDto req = new UserRegistrationDto("", "", "", null, "");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUser_success() throws Exception {
        UserLoginDto login = new UserLoginDto();
        login.setUsername("alice");
        login.setPassword("secret123");

        User user = sampleUser(UUID.randomUUID(), "alice");
        UserDto dto = sampleUserDto(user.getId(), "alice");

        when(userService.findByUsernameAndPassword("alice", "secret123")).thenReturn(user);
        when(userService.generateToken(user)).thenReturn("tok123");
        when(userMapper.userToDto(user)).thenReturn(dto);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.token", is("tok123")))
                .andExpect(jsonPath("$.user.username", is("alice")));
    }

    @Test
    void loginUser_invalidCredentials_returnsUnauthorized() throws Exception {
        UserLoginDto login = new UserLoginDto();
        login.setUsername("noone");
        login.setPassword("bad");

        when(userService.findByUsernameAndPassword("noone", "bad")).thenThrow(new UserNotFoundException("Invalid"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    void loginUser_validationFails_missingPassword() throws Exception {
        UserLoginDto login = new UserLoginDto();
        login.setUsername("alice");
        login.setPassword("");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCurrentUser_success() throws Exception {
        UUID id = UUID.randomUUID();
        String tokenHeader = "Bearer tok";
        String token = "tok";

        User user = sampleUser(id, "alice");
        UserDto dto = sampleUserDto(id, "alice");

        when(jwtTokenReader.getUserId(token)).thenReturn(id.toString());
        when(userService.findById(id)).thenReturn(user);
        when(userMapper.userToDto(user)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", tokenHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.username", is("alice")));
    }

    @Test
    void getCurrentUser_notFound_returnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        String tokenHeader = "Bearer tok";
        String token = "tok";

        when(jwtTokenReader.getUserId(token)).thenReturn(id.toString());
        when(userService.findById(id)).thenThrow(new UserNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", tokenHeader))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCurrentUser_invalidToken_returnsBadRequest() throws Exception {
        String tokenHeader = "Bearer bad";
        String token = "bad";

        when(jwtTokenReader.getUserId(token)).thenReturn("not-a-uuid");

        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", tokenHeader))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_success() throws Exception {
        UUID id = UUID.randomUUID();
        String tokenHeader = "Bearer tt";
        String token = "tt";

        when(jwtTokenReader.getUserId(token)).thenReturn(id.toString());
        doNothing().when(userService).deleteUser(id);

        mockMvc.perform(delete("/api/v1/auth").header("Authorization", tokenHeader))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_notFound_returnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        String tokenHeader = "Bearer tt";
        String token = "tt";

        when(jwtTokenReader.getUserId(token)).thenReturn(id.toString());
        doThrow(new UserNotFoundException("Not found")).when(userService).deleteUser(id);

        mockMvc.perform(delete("/api/v1/auth").header("Authorization", tokenHeader))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void deleteUser_invalidToken_returnsBadRequest() throws Exception {
        String tokenHeader = "Bearer bad";
        String token = "bad";

        when(jwtTokenReader.getUserId(token)).thenReturn("not-a-uuid");

        mockMvc.perform(delete("/api/v1/auth").header("Authorization", tokenHeader))
                .andExpect(status().isBadRequest());
    }
}

