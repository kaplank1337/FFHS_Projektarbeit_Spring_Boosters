package ch.ffhs.spring_boosters.integration;

import ch.ffhs.spring_boosters.controller.UserController;
import ch.ffhs.spring_boosters.controller.dto.UserLoginDto;
import ch.ffhs.spring_boosters.controller.entity.User;
import ch.ffhs.spring_boosters.repository.UserRepository;
import ch.ffhs.spring_boosters.service.UserService;
import ch.ffhs.spring_boosters.test.TestFlywayInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { TestFlywayInitializer.class })
@ActiveProfiles("integrationtest")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserController userController;

    @Autowired
    private TestRestTemplate restTemplate;

    // Helper to create a minimal valid user entity used in tests
    private User newTestUser(String username) {
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash("$2a$10$placeholderhash"); // password hash placeholder
        u.setFirstName("Test");
        u.setLastName("User");
        u.setBirthDate(LocalDate.of(1990, 1, 1));
        return u;
    }

    @BeforeEach
    void beforeEach() {
        // Ensure test DB has required seed users from Flyway
        // but also clean up any users we create with predictable prefix
        List<User> all = userRepository.findAll();
        for (User u : all) {
            if (u.getUsername() != null && u.getUsername().startsWith("tmp.test.")) {
                userRepository.deleteById(u.getId());
            }
        }
    }

    @Test
    @DisplayName("1. Repository load: seeded users exist")
    void t1_seededUsersExist() {
        Optional<User> john = userRepository.findByUsername("john.doe");
        assertTrue(john.isPresent(), "john.doe must be present from seed data");
    }

    @Test
    @DisplayName("2. Service: findByUsername returns user")
    void t2_serviceFindByUsername() throws Exception {
        User u = userService.findByUsername("john.doe");
        assertNotNull(u);
        assertEquals("john.doe", u.getUsername());
    }

    @Test
    @DisplayName("3. Repository: existsByUsername true/false")
    void t3_existsByUsername() {
        assertTrue(userRepository.existsByUsername("john.doe"));
        assertFalse(userRepository.existsByUsername("tmp.test.nonexistent"));
    }

    @Test
    @DisplayName("4. Service: generateToken produces non-empty token")
    void t4_generateToken() throws Exception {
        User u = userService.findByUsername("john.doe");
        String token = userService.generateToken(u);
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("5. Controller wiring: controller is present")
    void t5_controllerPresent() {
        assertNotNull(userController);
    }

    @Test
    @DisplayName("6. Create user via repository and read back")
    void t6_createAndReadUserRepo() {
        String uname = "tmp.test.createuser";
        User u = newTestUser(uname);
        User saved = userRepository.save(u);
        assertNotNull(saved.getId());

        Optional<User> fetched = userRepository.findById(saved.getId());
        assertTrue(fetched.isPresent());
        assertEquals(uname, fetched.get().getUsername());

        // cleanup
        userRepository.deleteById(saved.getId());
    }

    @Test
    @DisplayName("7. Service: registerUser prevents duplicates")
    void t7_registerUserDuplicate() throws Exception {
        String uname = "tmp.test.duplicate";
        User u = newTestUser(uname);
        User first = userService.registerUser(u);
        assertNotNull(first.getId());

        // second registration should throw UserAlreadyExistException
        Exception ex = assertThrows(Exception.class, () -> userService.registerUser(newTestUser(uname)));
        assertTrue(ex.getMessage().toLowerCase().contains("exists") || ex.getClass().getSimpleName().toLowerCase().contains("already"));

        userRepository.deleteById(first.getId());
    }

    @Test
    @DisplayName("8. Service: findByUsernameAndPassword throws for wrong password")
    void t8_findByUsernameAndPassword_wrongPassword() throws Exception {
        // Use known seeded user 'john.doe' and wrong password
        assertThrows(Exception.class, () -> userService.findByUsernameAndPassword("john.doe", "wrongpass"));
    }

    @Test
    @DisplayName("9. REST: login endpoint returns 200 and token for valid credentials")
    void t9_restLogin_validCredentials() {
        // Use TestRestTemplate to call /api/v1/users/login if exposed
        UserLoginDto login = new UserLoginDto();
        login.setUsername("john.doe");
        login.setPassword("user123"); // matches seeded hash in Flyway

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserLoginDto> req = new HttpEntity<>(login, headers);

        ResponseEntity<String> resp = restTemplate.postForEntity("/api/v1/auth/login", req, String.class);
        assertTrue(resp.getStatusCode().is2xxSuccessful(), "Login should succeed for seeded user");
        assertNotNull(resp.getBody());
        assertFalse(resp.getBody().isBlank());
    }

    @Test
    @DisplayName("10. REST: login with wrong credentials returns 4xx")
    void t10_restLogin_invalidCredentials() {
        UserLoginDto login = new UserLoginDto();
        login.setUsername("john.doe");
        login.setPassword("nope");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserLoginDto> req = new HttpEntity<>(login, headers);

        ResponseEntity<String> resp = restTemplate.postForEntity("/api/v1/auth/login", req, String.class);
        assertTrue(resp.getStatusCode().is4xxClientError(), "Invalid login should be client error");
    }

    @Test
    @DisplayName("11. REST: register endpoint creates user and returns 201")
    void t11_restRegister_createsUser() {
        User u = newTestUser("tmp.test.register");
        // build JSON payload manually
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = String.format("{\"username\":\"%s\",\"password\":\"pass123\",\"firstName\":\"%s\",\"lastName\":\"%s\",\"birthDate\":\"1990-01-01\"}",
                u.getUsername(), u.getFirstName(), u.getLastName());
        HttpEntity<String> req = new HttpEntity<>(body, headers);

        ResponseEntity<String> resp = restTemplate.postForEntity("/api/v1/auth/register", req, String.class);
        assertTrue(resp.getStatusCode().is2xxSuccessful() || resp.getStatusCode()==HttpStatus.CREATED);

        // assert user now exists
        assertTrue(userRepository.existsByUsername(u.getUsername()));

        // cleanup
        userRepository.findByUsername(u.getUsername()).ifPresent(x -> userRepository.deleteById(x.getId()));
    }

    @Test
    @DisplayName("12. REST: register invalid payload returns 4xx")
    void t12_restRegister_invalidPayload() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String bad = "{\"username\":\"\"}"; // empty username
        HttpEntity<String> req = new HttpEntity<>(bad, headers);
        ResponseEntity<String> resp = restTemplate.postForEntity("/api/v1/auth/register", req, String.class);
        assertTrue(resp.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("13. Service: deleteUser removes user and subsequent find fails")
    void t13_deleteUser() throws Exception {
        User u = userRepository.save(newTestUser("tmp.test.delete"));
        UUID id = u.getId();
        assertNotNull(id);

        userService.deleteUser(id);
        assertFalse(userRepository.findById(id).isPresent());
    }

    @Test
    @DisplayName("14. Repository: findAll returns non-empty list")
    void t14_findAllUsers() {
        List<User> all = userRepository.findAll();
        assertNotNull(all);
        assertTrue(all.size() >= 1, "There should be at least one seeded user");
    }

    @Test
    @DisplayName("15. Service: findById returns correct user")
    void t15_findById() throws Exception {
        User john = userService.findByUsername("john.doe");
        User byId = userService.findById(john.getId());
        assertEquals(john.getId(), byId.getId());
    }

    @Test
    @DisplayName("16. Token generation and decode roundtrip using JwtTokenReader via controller flow")
    void t16_tokenRoundtrip() throws Exception {
        // generate token
        User john = userService.findByUsername("john.doe");
        String token = userService.generateToken(john);
        assertNotNull(token);

        // call controller endpoint that expects Authorization header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token.startsWith("Bearer ") ? token : "Bearer " + token);
        HttpEntity<Void> req = new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange("/api/v1/auth/me", HttpMethod.GET, req, String.class);
        // endpoint should return 200 for a valid token
        assertTrue(resp.getStatusCode().is2xxSuccessful());
    }

    @Test
    @DisplayName("17. Service: existsByUsername matches repository")
    void t17_existsByUsername_consistency() {
        User john = userRepository.findByUsername("john.doe").orElseThrow();
        assertTrue(userService.existsByUsername(john.getUsername()));
    }

    @Test
    @DisplayName("18. Negative: findByUsername throws when user missing")
    void t18_findByUsername_notFound() {
        assertThrows(Exception.class, () -> userService.findByUsername("tmp.test.noSuchUser"));
    }

    @Test
    @DisplayName("19. Negative: deleteUser throws when user missing")
    void t19_deleteUser_notFound() {
        assertThrows(Exception.class, () -> userService.deleteUser(UUID.randomUUID()));
    }

    @Test
    @DisplayName("20. Register and login flow end-to-end (create then login)")
    void t20_registerThenLogin() {
        String uname = "tmp.test.endtoend";
        // register
        String body = String.format("{\"username\":\"%s\",\"password\":\"pass123\",\"firstName\":\"A\",\"lastName\":\"B\",\"birthDate\":\"1990-01-01\"}", uname);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> r = restTemplate.postForEntity("/api/v1/auth/register", new HttpEntity<>(body, headers), String.class);
        assertTrue(r.getStatusCode().is2xxSuccessful() || r.getStatusCode()==HttpStatus.CREATED);

        // login
        UserLoginDto login = new UserLoginDto();
        login.setUsername(uname);
        login.setPassword("pass123");
        ResponseEntity<String> loginResp = restTemplate.postForEntity("/api/v1/auth/login", new HttpEntity<>(login, headers), String.class);
        assertTrue(loginResp.getStatusCode().is2xxSuccessful());
        assertNotNull(loginResp.getBody());

        // cleanup
        userRepository.findByUsername(uname).ifPresent(x -> userRepository.deleteById(x.getId()));
    }

    @Test
    @DisplayName("21. Register with extremely long username returns 4xx")
    void t21_register_longUsername() {
        String uname = "tmp.test." + "x".repeat(300);
        String body = String.format("{\"username\":\"%s\",\"password\":\"p\",\"firstName\":\"A\",\"lastName\":\"B\",\"birthDate\":\"1990-01-01\"}", uname);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> r = restTemplate.postForEntity("/api/v1/auth/register", new HttpEntity<>(body, headers), String.class);
        assertTrue(r.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("22. Login with missing body returns 4xx")
    void t22_login_missingBody() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> r = restTemplate.postForEntity("/api/v1/auth/login", new HttpEntity<>("", headers), String.class);
        assertFalse(r.getStatusCode().is2xxSuccessful(), "Missing body must not result in success");
    }
}
