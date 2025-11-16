/*
package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.TestcontainersConfiguration;
import ch.ffhs.spring_boosters.controller.dto.UserRegistrationDto;
import ch.ffhs.spring_boosters.controller.dto.UserLoginDto;
import ch.ffhs.spring_boosters.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@AutoConfigureWebMvc
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Clean up database before each test
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterNewUser() throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto(
                "testuser",
                "John",
                "Doe",
                LocalDate.of(1990, 5, 15),
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("User registered successfully")))
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.userId", notNullValue()));
    }

    @Test
    void shouldRejectDuplicateUsername() throws Exception {
        // First registration
        UserRegistrationDto firstUser = new UserRegistrationDto(
                "testuser",
                "John",
                "Doe",
                LocalDate.of(1990, 5, 15),
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstUser)))
                .andExpect(status().isCreated());

        // Second registration with same username
        UserRegistrationDto duplicateUser = new UserRegistrationDto(
                "testuser",
                "Jane",
                "Smith",
                LocalDate.of(1985, 3, 20),
                "password456"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Username already exists")));
    }

    @Test
    void shouldLoginWithValidCredentials() throws Exception {
        // Register a user first
        UserRegistrationDto registrationDto = new UserRegistrationDto(
                "testuser",
                "John",
                "Doe",
                LocalDate.of(1990, 5, 15),
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated());

        // Now try to login
        UserLoginDto loginDto = new UserLoginDto("testuser", "password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Login successful")))
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.role", is("USER")));
    }

    @Test
    void shouldRejectInvalidCredentials() throws Exception {
        UserLoginDto loginDto = new UserLoginDto("nonexistent", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Invalid username or password")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void shouldReturnCurrentUserInfo() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.username", is("testuser")));
    }

    @Test
    void shouldRequireAuthenticationForProtectedEndpoint() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldValidateRegistrationInput() throws Exception {
        UserRegistrationDto invalidDto = new UserRegistrationDto(
                "", // empty username
                "",  // empty first name
                "",  // empty last name
                null, // null birth date
                "123" // too short password
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}
*/
