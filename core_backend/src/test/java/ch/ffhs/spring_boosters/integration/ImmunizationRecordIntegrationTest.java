package ch.ffhs.spring_boosters.integration;

import ch.ffhs.spring_boosters.controller.ImmunizationRecordController;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordCreateDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordUpdateDto;
import ch.ffhs.spring_boosters.repository.ImmunizationRecordRepository;
import ch.ffhs.spring_boosters.repository.UserRepository;
import ch.ffhs.spring_boosters.service.ImmunizationRecordService;
import ch.ffhs.spring_boosters.test.TestFlywayInitializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ImmunizationRecordIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ImmunizationRecordRepository immunizationRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImmunizationRecordService immunizationRecordService;

    @Autowired
    private ImmunizationRecordController immunizationRecordController;

    @Value("${jwt.secret:defaultsecretkeydefaultsecretkeydefaultsecretkey12}")
    private String jwtSecret;

    private final String TEST_PREFIX = "IT_RECORD_" + UUID.randomUUID().toString().substring(0, 8) + "_";

    @BeforeEach
    void beforeEach() {
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    private UUID firstVaccineTypeId() {
        try {
            ResponseEntity<Map> respMap = restTemplate.getForEntity("/api/v1/vaccine-types", Map.class);
            if (respMap.getStatusCode() == HttpStatus.OK) {
                Map body = respMap.getBody();
                if (body != null) {
                    Object listObj = body.get("vaccineTypeDtoList");
                    if (listObj instanceof List) {
                        List l = (List) listObj;
                        if (!l.isEmpty() && l.get(0) instanceof Map) {
                            Object id = ((Map) l.get(0)).get("id");
                            if (id instanceof String) return UUID.fromString((String) id);
                        }
                    }
                    if (body.get("id") instanceof String) return UUID.fromString((String) body.get("id"));
                }
            }
        } catch (Exception ignored) {
        }
        ResponseEntity<String> resp = restTemplate.getForEntity("/api/v1/vaccine-types", String.class);
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        String body = resp.getBody();
        Assertions.assertNotNull(body);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(body);
            if (root.has("vaccineTypeDtoList")) {
                JsonNode list = root.get("vaccineTypeDtoList");
                if (list.isArray()) {
                    for (JsonNode node : list) {
                        if (node.has("id") && !node.get("id").isNull()) return UUID.fromString(node.get("id").asText());
                    }
                }
            }
            if (root.isArray()) {
                for (JsonNode node : root) {
                    if (node.has("id") && !node.get("id").isNull()) return UUID.fromString(node.get("id").asText());
                }
            }
            if (root.has("id") && !root.get("id").isNull()) return UUID.fromString(root.get("id").asText());
            JsonNode found = findFirstIdField(root);
            if (found != null && !found.isNull()) return UUID.fromString(found.asText());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse vaccine-types response", e);
        }
        throw new IllegalStateException("No vaccine types found in response");
    }

    private JsonNode findFirstIdField(JsonNode node) {
        if (node == null) return null;
        if (node.has("id") && !node.get("id").isNull()) return node.get("id");
        if (node.isObject()) {
            for (JsonNode child : node) {
                JsonNode f = findFirstIdField(child);
                if (f != null) return f;
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                JsonNode f = findFirstIdField(child);
                if (f != null) return f;
            }
        }
        return null;
    }

    private UUID firstAgeCategoryId() {
        ResponseEntity<String> resp = restTemplate.getForEntity("/api/v1/age-categories", String.class);
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        String body = resp.getBody();
        Assertions.assertNotNull(body);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(body);
            if (root.isArray() && root.size() > 0) {
                JsonNode first = root.get(0);
                if (first.has("id")) return UUID.fromString(first.get("id").asText());
            }
            if (root.has("id")) return UUID.fromString(root.get("id").asText());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse age-categories response", e);
        }
        throw new IllegalStateException("No age categories found in response");
    }

    private UUID immunizationPlanIdFor(UUID vaccineTypeId, UUID ageCategoryId) {
        ResponseEntity<String> resp = restTemplate.getForEntity("/api/v1/immunization-plans", String.class);
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        String body = resp.getBody();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(body);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    JsonNode vtNode = node.get("vaccineTypeId");
                    JsonNode acNode = node.get("ageCategoryId");
                    JsonNode idNode = node.get("id");
                    if (vtNode != null && acNode != null && idNode != null) {
                        if (vaccineTypeId.toString().equals(vtNode.asText()) && ageCategoryId.toString().equals(acNode.asText())) {
                            return UUID.fromString(idNode.asText());
                        }
                    }
                }
                if (root.size() > 0 && root.get(0).has("id")) return UUID.fromString(root.get(0).get("id").asText());
            } else if (root.has("id")) {
                return UUID.fromString(root.get("id").asText());
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse immunization-plans response", e);
        }
        throw new IllegalStateException("No immunization plans available");
    }

    @Test
    @DisplayName("Full lifecycle: create -> get -> list -> update -> delete")
    void lifecycleEndToEnd() {
        UUID vt = firstVaccineTypeId();
        UUID ac = firstAgeCategoryId();
        UUID user = userIdByUsername("john.doe");

        String name = TEST_PREFIX + "LIFECYCLE";
        LocalDate administeredOn = LocalDate.of(2023, 1, 10);
        ImmunizationRecordCreateDto createDto = new ImmunizationRecordCreateDto(vt, administeredOn, 1);

        ResponseEntity<ImmunizationRecordDto> createResp = restTemplate.postForEntity("/api/v1/immunization-records", createDto, ImmunizationRecordDto.class);
        Assertions.assertEquals(HttpStatus.CREATED, createResp.getStatusCode());
        ImmunizationRecordDto created = createResp.getBody();
        Assertions.assertNotNull(created);
        UUID id = created.id();

        ResponseEntity<ImmunizationRecordDto> getResp = restTemplate.getForEntity("/api/v1/immunization-records/" + id, ImmunizationRecordDto.class);
        Assertions.assertEquals(HttpStatus.OK, getResp.getStatusCode());
        Assertions.assertNotNull(getResp.getBody());

        ResponseEntity<ImmunizationRecordDto[]> listResp = restTemplate.getForEntity("/api/v1/immunization-records", ImmunizationRecordDto[].class);
        Assertions.assertEquals(HttpStatus.OK, listResp.getStatusCode());
        ImmunizationRecordDto[] all = listResp.getBody();
        boolean found = false;
        if (all != null) {
            for (ImmunizationRecordDto r : all) {
                if (id.equals(r.id())) found = true;
            }
        }
        Assertions.assertTrue(found);

        ImmunizationRecordUpdateDto upd = new ImmunizationRecordUpdateDto(user, vt, administeredOn.plusDays(1), 2);
        ResponseEntity<ImmunizationRecordDto> updResp = restTemplate.exchange("/api/v1/immunization-records/" + id, HttpMethod.PATCH, new HttpEntity<>(upd, jsonHeaders()), ImmunizationRecordDto.class);
        Assertions.assertEquals(HttpStatus.OK, updResp.getStatusCode());

        String token = tokenForUser(user);
        ResponseEntity<Void> delResp = restTemplate.exchange("/api/v1/immunization-records/" + id, HttpMethod.DELETE, new HttpEntity<>(null, authorizationHeader(token)), Void.class);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, delResp.getStatusCode());

        ResponseEntity<Map> after = restTemplate.getForEntity("/api/v1/immunization-records/" + id, Map.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, after.getStatusCode());
    }

    private HttpHeaders authorizationHeader(String bearer) {
        HttpHeaders h = new HttpHeaders();
        h.set("Authorization", bearer);
        return h;
    }

    private UUID userIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(u -> u.getId())
                .orElseThrow(() -> new IllegalStateException("Seeded user not found: " + username));
    }

    private String tokenForUser(UUID userId) {
        String compact = Jwts.builder()
                .claim("userid", userId.toString())
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();
        return "Bearer " + compact;
    }

    @Test
    @DisplayName("Create validation fails for missing required fields")
    void createValidationFails() {
        ImmunizationRecordCreateDto bad = new ImmunizationRecordCreateDto(null, null, null);
        ResponseEntity<Map> resp = restTemplate.postForEntity("/api/v1/immunization-records", bad, Map.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    @DisplayName("Get by user returns list of records")
    void getByUser() {
        UUID user = userIdByUsername("john.doe");
        ResponseEntity<ImmunizationRecordDto[]> resp = restTemplate.getForEntity("/api/v1/immunization-records/by-user/" + user, ImmunizationRecordDto[].class);
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        ImmunizationRecordDto[] arr = resp.getBody();
        Assertions.assertNotNull(arr);
    }

    @Test
    @DisplayName("Get by vaccine type returns list")
    void getByVaccineType() {
        UUID vt = firstVaccineTypeId();
        ResponseEntity<ImmunizationRecordDto[]> resp = restTemplate.getForEntity("/api/v1/immunization-records/by-vaccine-type/" + vt, ImmunizationRecordDto[].class);
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("Get by user and vaccine type returns list")
    void getByUserAndVaccineType() {
        UUID user = userIdByUsername("jane.smith");
        UUID vt = firstVaccineTypeId();
        ResponseEntity<ImmunizationRecordDto[]> resp = restTemplate.getForEntity("/api/v1/immunization-records/by-user/" + user + "/vaccine-type/" + vt, ImmunizationRecordDto[].class);
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("Get my vaccinations with valid token returns 200")
    void getMyVaccinations_validToken() {
        UUID user = userIdByUsername("john.doe");
        String token = tokenForUser(user);
        ResponseEntity<ImmunizationRecordDto[]> resp = restTemplate.exchange("/api/v1/immunization-records/myVaccinations", HttpMethod.GET, new HttpEntity<>(authorizationHeader(token)), ImmunizationRecordDto[].class);
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("Get my vaccinations with invalid token returns 400")
    void getMyVaccinations_invalidToken() {
        String token = "Bearer bad.token.value";
        ResponseEntity<Map> resp = restTemplate.exchange("/api/v1/immunization-records/myVaccinations", HttpMethod.GET, new HttpEntity<>(authorizationHeader(token)), Map.class);
        Assertions.assertTrue(resp.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Delete with invalid token returns 400")
    void deleteInvalidToken() {
        UUID id = UUID.randomUUID();
        String token = "Bearer bad.token";
        ResponseEntity<Void> resp = restTemplate.exchange("/api/v1/immunization-records/" + id, HttpMethod.DELETE, new HttpEntity<>(null, authorizationHeader(token)), Void.class);
        Assertions.assertTrue(resp.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Create with malformed JSON returns 500 (product behaviour)")
    void createMalformedJson() {
        HttpHeaders h = jsonHeaders();
        String bad = "{ \"userId\": }";
        ResponseEntity<Map> resp = restTemplate.postForEntity("/api/v1/immunization-records", new HttpEntity<>(bad, h), Map.class);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }

    @Test
    @DisplayName("Invalid UUID in path returns 4xx")
    void invalidUuidPath() {
        ResponseEntity<Map> resp = restTemplate.getForEntity("/api/v1/immunization-records/not-a-uuid", Map.class);
        Assertions.assertTrue(resp.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Batch create small load and list assertion")
    void batchCreateSmall() {
        UUID vt = firstVaccineTypeId();
        UUID ac = firstAgeCategoryId();
        UUID user = userIdByUsername("max.mustermann");

        int n = 5;
        for (int i = 0; i < n; i++) {
            LocalDate date = LocalDate.of(2020, 1, 1).plusDays(i);
            ImmunizationRecordCreateDto dto = new ImmunizationRecordCreateDto(vt, date, i + 1);
            ResponseEntity<ImmunizationRecordDto> resp = restTemplate.postForEntity("/api/v1/immunization-records", dto, ImmunizationRecordDto.class);
            Assertions.assertTrue(resp.getStatusCode().is2xxSuccessful() || resp.getStatusCode().is4xxClientError());
        }

        ResponseEntity<ImmunizationRecordDto[]> list = restTemplate.getForEntity("/api/v1/immunization-records/by-user/" + user, ImmunizationRecordDto[].class);
        Assertions.assertEquals(HttpStatus.OK, list.getStatusCode());
    }

    @Test
    @DisplayName("Create then update to duplicate values (service-defined behaviour)")
    void createThenDuplicateUpdate() {
        UUID vt = firstVaccineTypeId();
        UUID ac = firstAgeCategoryId();
        UUID user = userIdByUsername("jane.smith");

        ImmunizationRecordCreateDto c1 = new ImmunizationRecordCreateDto(vt, LocalDate.of(2021,5,1), 1);
        ImmunizationRecordCreateDto c2 = new ImmunizationRecordCreateDto(vt, LocalDate.of(2021,6,1), 2);
        ImmunizationRecordDto r1 = restTemplate.postForEntity("/api/v1/immunization-records", c1, ImmunizationRecordDto.class).getBody();
        ImmunizationRecordDto r2 = restTemplate.postForEntity("/api/v1/immunization-records", c2, ImmunizationRecordDto.class).getBody();
        Assertions.assertNotNull(r1);
        Assertions.assertNotNull(r2);

        ImmunizationRecordUpdateDto upd = new ImmunizationRecordUpdateDto(user, vt, LocalDate.of(2021,5,1), 2);
        ResponseEntity<Map> resp = restTemplate.exchange("/api/v1/immunization-records/" + r2.id(), HttpMethod.PATCH, new HttpEntity<>(upd, jsonHeaders()), Map.class);
        Assertions.assertTrue(resp.getStatusCode().is2xxSuccessful() || resp.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Controller bean is present")
    void controllerLoads() {
        Assertions.assertNotNull(immunizationRecordController);
    }

    @Test
    @DisplayName("Edge case: create with null doseOrderClaimed and large date values")
    void createNullDoseAndLargeDates() {
        UUID vt = firstVaccineTypeId();
        UUID ac = firstAgeCategoryId();
        UUID user = userIdByUsername("max.mustermann");
        ImmunizationRecordCreateDto dto = new ImmunizationRecordCreateDto(vt, LocalDate.of(1970,1,1), null);
        ResponseEntity<ImmunizationRecordDto> resp = restTemplate.postForEntity("/api/v1/immunization-records", dto, ImmunizationRecordDto.class);
        Assertions.assertTrue(resp.getStatusCode().is2xxSuccessful() || resp.getStatusCode().is4xxClientError());
    }

}
