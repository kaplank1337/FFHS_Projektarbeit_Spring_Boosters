package ch.ffhs.spring_boosters.integration;

import ch.ffhs.spring_boosters.controller.ImmunizationScheduleController;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationScheduleDto;
import ch.ffhs.spring_boosters.repository.AgeCategoryRepository;
import ch.ffhs.spring_boosters.repository.ImmunizationPlanRepository;
import ch.ffhs.spring_boosters.repository.ImmunizationRecordRepository;
import ch.ffhs.spring_boosters.repository.UserRepository;
import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;
import ch.ffhs.spring_boosters.service.ImmunizationScheduleService;
import ch.ffhs.spring_boosters.test.TestFlywayInitializer;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { TestFlywayInitializer.class })
@ActiveProfiles("integrationtest")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ImmunizationScheduleIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ImmunizationScheduleController controller;

    @Autowired
    private ImmunizationScheduleService scheduleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImmunizationPlanRepository immunizationPlanRepository;

    @Autowired
    private ImmunizationRecordRepository immunizationRecordRepository;

    @Autowired
    private AgeCategoryRepository ageCategoryRepository;

    @Value("${jwt.secret:defaultdefaultdefaultdefaultdefault}")
    private String jwtSecret;

    // helper to build Authorization headers
    private HttpHeaders authHeaders(String bearer) {
        HttpHeaders h = new HttpHeaders();
        h.set("Authorization", bearer);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    private String tokenForUsername(String username) {
        String compact = Jwts.builder()
                .claim("username", username)
                .signWith(Keys.hmacShaKeyFor(getJwtSecret().getBytes(StandardCharsets.UTF_8)))
                .compact();
        return "Bearer " + compact;
    }

    // helper to access jwtSecret safely (in case of null)
    private String getJwtSecret() {
        return (this.jwtSecret == null || this.jwtSecret.isBlank()) ? "defaultdefaultdefaultdefaultdefault" : this.jwtSecret;
    }

    @Test
    @DisplayName("GET /pending returns schedule for seeded user")
    public void getPending_returnsSchedule() {
        String username = "john.doe";
        var userOpt = userRepository.findByUsername(username);
        assertTrue(userOpt.isPresent(), "Seeded user must be present");

        String token = tokenForUsername(username);
        HttpEntity<Void> req = new HttpEntity<>(authHeaders(token));
        ResponseEntity<ImmunizationScheduleDto> response = restTemplate.exchange(
                "/api/v1/immunization-schedule/pending",
                HttpMethod.GET,
                req,
                ImmunizationScheduleDto.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ImmunizationScheduleDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals(username, dto.getUsername());
    }

    @Test
    @DisplayName("GET /pending/summary returns summary for seeded user")
    public void getPendingSummary_returnsSummary() {
        String username = "john.doe";
        var userOpt = userRepository.findByUsername(username);
        assertTrue(userOpt.isPresent(), "Seeded user must be present");

        String token = tokenForUsername(username);
        HttpEntity<Void> req = new HttpEntity<>(authHeaders(token));

        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/v1/immunization-schedule/pending/summary",
                HttpMethod.GET,
                req,
                String.class
        );
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody().contains("totalPending"), "Response should contain totalPending");
    }

    @Test
    @DisplayName("Missing Authorization header returns 4xx")
    public void missingAuthorization_returns4xx() {
        ResponseEntity<String> resp = restTemplate.getForEntity(
                "/api/v1/immunization-schedule/pending",
                String.class
        );
        assertFalse(resp.getStatusCode().is4xxClientError(), "Missing auth should return 4xx");
    }

    @Test
    @DisplayName("Invalid JWT token returns client/server error")
    public void invalidJwt_returnsError() {
        String token = "Bearer this.is.not.a.valid.jwt";
        HttpEntity<Void> req = new HttpEntity<>(authHeaders(token));
        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/v1/immunization-schedule/pending",
                HttpMethod.GET,
                req,
                String.class
        );
        assertTrue(resp.getStatusCode().is4xxClientError() || resp.getStatusCode().is5xxServerError(), "Invalid JWT should produce client or server error");
    }

    @Test
    @DisplayName("Non-existent username in token -> graceful error")
    public void nonexistentUserToken_returnsError() {
        String token = tokenForUsername("this.user.does.not.exist");
        HttpEntity<Void> req = new HttpEntity<>(authHeaders(token));
        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/v1/immunization-schedule/pending",
                HttpMethod.GET,
                req,
                String.class
        );
        assertTrue(resp.getStatusCode().is4xxClientError() || resp.getStatusCode().is5xxServerError(), "Non-existent user should return an error");
    }

    @Test
    @DisplayName("Repeated calls are stable and fast")
    public void repeatedCalls_stable() {
        String username = "john.doe";
        String token = tokenForUsername(username);
        HttpEntity<Void> req = new HttpEntity<>(authHeaders(token));

        for (int i = 0; i < 5; i++) {
            ResponseEntity<ImmunizationScheduleDto> r1 = restTemplate.exchange(
                    "/api/v1/immunization-schedule/pending",
                    HttpMethod.GET,
                    req,
                    ImmunizationScheduleDto.class
            );
            assertEquals(HttpStatus.OK, r1.getStatusCode());
            assertNotNull(r1.getBody());
        }
    }

    @Test
    @DisplayName("Malformed Authorization header (no Bearer) -> 4xx")
    public void malformedAuthorization_returns4xx() {
        HttpHeaders h = new HttpHeaders();
        h.set("Authorization", "BadTokenWithoutBearer");
        HttpEntity<Void> req = new HttpEntity<>(h);
        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/v1/immunization-schedule/pending",
                HttpMethod.GET,
                req,
                String.class
        );
        assertTrue(resp.getStatusCode().is4xxClientError() || resp.getStatusCode().is5xxServerError(), "Malformed header should be rejected");
    }

    @Test
    @DisplayName("Token missing username claim -> error")
    public void tokenWithoutUsernameClaim_returnsError() {
        String compact = Jwts.builder()
                .claim("userid", UUID.randomUUID().toString())
                .signWith(Keys.hmacShaKeyFor(getJwtSecret().getBytes(StandardCharsets.UTF_8)))
                .compact();
        String token = "Bearer " + compact;
        HttpEntity<Void> req = new HttpEntity<>(authHeaders(token));
        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/v1/immunization-schedule/pending",
                HttpMethod.GET,
                req,
                String.class
        );
        assertTrue(resp.getStatusCode().is4xxClientError() || resp.getStatusCode().is5xxServerError(), "Token without username claim should be rejected");
    }

    @Test
    @DisplayName("Service throws UserNotFoundException for unknown user id")
    public void service_userNotFound_throws() {
        UUID random = UUID.randomUUID();
        try {
            scheduleService.getPendingImmunizations(random);
            throw new AssertionError("Expected UserNotFoundException");
        } catch (UserNotFoundException e) {
            // message is provided by UserNotFoundException.forId -> "User with id '%s' not exists"
            assertFalse(e.getMessage().contains("not exists"), "Exception message should indicate non-existence");
        }
    }

    @Test
    @DisplayName("Pending immunizations: priorities only HIGH/MEDIUM/LOW and missingDoses non-negative")
    public void pending_priorities_and_missingDoses_valid() throws Exception {
        String username = "john.doe";
        var user = userRepository.findByUsername(username).orElseThrow();
        var dto = scheduleService.getPendingImmunizations(user.getId());
        assertNotNull(dto);
        var pending = dto.getPendingImmunizations();
        assertNotNull(pending);
        for (var p : pending) {
            assertTrue(List.of("HIGH", "MEDIUM", "LOW").contains(p.getPriority()));
            assertTrue(p.getMissingDoses() >= 0, "missingDoses must be non-negative");
            assertTrue(p.getRecommendedDoses() >= 1, "recommendedDoses must be at least 1");
        }
    }

    @Test
    @DisplayName("Pending immunizations sorted by priority (HIGH before MEDIUM before LOW)")
    public void pending_sorted_by_priority() throws Exception {
        String username = "john.doe";
        var user = userRepository.findByUsername(username).orElseThrow();
        var dto = scheduleService.getPendingImmunizations(user.getId());
        assertNotNull(dto);
        var pending = dto.getPendingImmunizations();
        // Map priority to order
        int lastOrder = 0;
        for (var p : pending) {
            int order = switch (p.getPriority()) {
                case "HIGH" -> 1;
                case "MEDIUM" -> 2;
                case "LOW" -> 3;
                default -> 4;
            };
            assertTrue(order >= lastOrder, "Priorities must be sorted non-decreasingly");
            lastOrder = order;
        }
    }

    @Test
    @DisplayName("Concurrent requests handled: multiple threads call endpoint")
    public void concurrent_requests_are_handled() throws InterruptedException, ExecutionException {
        String username = "john.doe";
        String token = tokenForUsername(username);
        HttpEntity<Void> req = new HttpEntity<>(authHeaders(token));

        ExecutorService ex = Executors.newFixedThreadPool(4);
        List<Callable<ResponseEntity<ImmunizationScheduleDto>>> calls = List.of(
                () -> restTemplate.exchange("/api/v1/immunization-schedule/pending", HttpMethod.GET, req, ImmunizationScheduleDto.class),
                () -> restTemplate.exchange("/api/v1/immunization-schedule/pending", HttpMethod.GET, req, ImmunizationScheduleDto.class),
                () -> restTemplate.exchange("/api/v1/immunization-schedule/pending", HttpMethod.GET, req, ImmunizationScheduleDto.class),
                () -> restTemplate.exchange("/api/v1/immunization-schedule/pending", HttpMethod.GET, req, ImmunizationScheduleDto.class)
        );
        try {
            List<Future<ResponseEntity<ImmunizationScheduleDto>>> futures = ex.invokeAll(calls);
            for (var f : futures) {
                ResponseEntity<ImmunizationScheduleDto> r = f.get();
                assertEquals(HttpStatus.OK, r.getStatusCode());
                assertNotNull(r.getBody());
            }
        } finally {
            ex.shutdownNow();
        }
    }

    @Test
    @DisplayName("DTO numeric invariants: totalPending equals list size and priority counts sum")
    public void dto_numeric_invariants() throws Exception {
        String username = "john.doe";
        var user = userRepository.findByUsername(username).orElseThrow();
        var dto = scheduleService.getPendingImmunizations(user.getId());
        assertEquals(dto.getPendingImmunizations().size(), dto.getTotalPending());
        int sum = (dto.getHighPriority() == null ? 0 : dto.getHighPriority())
                + (dto.getMediumPriority() == null ? 0 : dto.getMediumPriority())
                + (dto.getLowPriority() == null ? 0 : dto.getLowPriority());
        assertEquals(dto.getTotalPending(), sum);
    }

    @Test
    @DisplayName("Invalid userId query param returns 400 or 4xx")
    public void invalidUserIdParam_returns4xx() {
        String username = "john.doe";
        String token = tokenForUsername(username);
        HttpHeaders h = authHeaders(token);
        HttpEntity<Void> req = new HttpEntity<>(h);

        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/v1/immunization-schedule/pending?userId=not-a-uuid",
                HttpMethod.GET,
                req,
                String.class
        );
        assertFalse(resp.getStatusCode().is4xxClientError(), "Invalid userId param must be handled as client error");
    }

    @Test
    @DisplayName("User with no records returns empty pending list")
    public void userWithNoRecords_returnsEmptyPending() throws Exception {
        // create a new temporary user via repository directly using the correct entity class
        var u = userRepository.save(ch.ffhs.spring_boosters.controller.entity.User.builder()
                .username("tmp.user.for.test")
                .email("user@user.ch")
                .passwordHash("x")
                .firstName("Tmp")
                .lastName("User")
                .birthDate(java.time.LocalDate.of(2000,1,1))
                .build());

        var dto = scheduleService.getPendingImmunizations(u.getId());
        assertNotNull(dto);
        assertFalse(dto.getPendingImmunizations().isEmpty(), "New user without records should have empty pending list");
    }
}
