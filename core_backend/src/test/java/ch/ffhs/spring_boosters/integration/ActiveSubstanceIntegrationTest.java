package ch.ffhs.spring_boosters.integration;

import ch.ffhs.spring_boosters.controller.dto.ActiveSubstanceCreateDto;
import ch.ffhs.spring_boosters.controller.dto.ActiveSubstanceDto;
import ch.ffhs.spring_boosters.controller.dto.ActiveSubstanceUpdateDto;
import ch.ffhs.spring_boosters.controller.ActiveSubstanceController;
import ch.ffhs.spring_boosters.controller.mapper.ActiveSubstanceMapper;
import ch.ffhs.spring_boosters.repository.ActiveSubstanceRepository;
import ch.ffhs.spring_boosters.service.ActiveSubstanceService;
import ch.ffhs.spring_boosters.test.TestFlywayInitializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TestFlywayInitializer.class)
@ActiveProfiles("integrationtest")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ActiveSubstanceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ActiveSubstanceController activeSubstanceController;

    @Autowired
    private ActiveSubstanceRepository activeSubstanceRepository;

    @Autowired
    private ActiveSubstanceService activeSubstanceService;

    @Autowired
    private ActiveSubstanceMapper activeSubstanceMapper;

    // Ensure test isolation: use a unique prefix for all test-created names
    private final String TEST_PREFIX = "IT_TEST_" + UUID.randomUUID().toString().substring(0, 8) + "_";

    @BeforeEach
    void beforeEach() {
        // Nothing destructive here (we keep seed data). Tests will create unique names.
    }

    @Test
    @DisplayName("Full lifecycle: create -> get -> list -> delete (happy path)")
    void lifecycleCreateGetListDelete() {
        String name = TEST_PREFIX + "CREATE_GET";
        ActiveSubstanceCreateDto createDto = new ActiveSubstanceCreateDto(name, new String[]{"s1", "s2"});

        // Create
        ResponseEntity<ActiveSubstanceDto> createResp = restTemplate.postForEntity(
                "/api/v1/active-substances",
                createDto,
                ActiveSubstanceDto.class
        );

        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ActiveSubstanceDto created = createResp.getBody();
        assertThat(created).isNotNull();
        assertThat(created.name()).isEqualTo(name);
        UUID id = created.id();

        // Get by id
        ResponseEntity<ActiveSubstanceDto> getResp = restTemplate.getForEntity("/api/v1/active-substances/" + id, ActiveSubstanceDto.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResp.getBody()).isNotNull();
        assertThat(getResp.getBody().name()).isEqualTo(name);

        // List all (should contain our created name somewhere)
        ResponseEntity<ActiveSubstanceDto[]> listResp = restTemplate.getForEntity("/api/v1/active-substances", ActiveSubstanceDto[].class);
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ActiveSubstanceDto[] all = listResp.getBody();
        assertThat(all).isNotEmpty();
        boolean found = List.of(all).stream().anyMatch(a -> name.equals(a.name()));
        assertThat(found).isTrue();

        // Delete
        ResponseEntity<Void> delResp = restTemplate.exchange("/api/v1/active-substances/" + id, HttpMethod.DELETE, null, Void.class);
        assertThat(delResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // After delete, GET should return 404
        ResponseEntity<Map> getAfterDel = restTemplate.getForEntity("/api/v1/active-substances/" + id, Map.class);
        assertThat(getAfterDel.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Create returns 400 when validation fails (missing name)")
    void createValidationFails() {
        // name is blank -> validation should fail
        ActiveSubstanceCreateDto createDto = new ActiveSubstanceCreateDto("", null);
        ResponseEntity<Map> resp = restTemplate.postForEntity("/api/v1/active-substances", createDto, Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map body = resp.getBody();
        assertThat(body).isNotNull();
        // Spring's default error payload contains 'error'
        assertThat(body.get("error")).isEqualTo("Bad Request");
    }

    @Test
    @DisplayName("Create duplicate returns 400 and descriptive error")
    void createDuplicateReturnsBadRequest() {
        String name = TEST_PREFIX + "DUP";
        ActiveSubstanceCreateDto createDto = new ActiveSubstanceCreateDto(name, new String[]{"d"});

        // First create should succeed
        ResponseEntity<ActiveSubstanceDto> first = restTemplate.postForEntity("/api/v1/active-substances", createDto, ActiveSubstanceDto.class);
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Second create with same name should fail
        ResponseEntity<Map> second = restTemplate.postForEntity("/api/v1/active-substances", createDto, Map.class);
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map body = second.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("error")).isEqualTo("Bad Request");
        assertThat(body.get("message")).isNotNull();
    }

    @Test
    @DisplayName("Get non-existing id returns 404")
    void getNotFound() {
        UUID id = UUID.randomUUID();
        ResponseEntity<Map> resp = restTemplate.getForEntity("/api/v1/active-substances/" + id, Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Update non-existing returns 404")
    void updateNotFound() {
        UUID id = UUID.randomUUID();
        ActiveSubstanceUpdateDto updateDto = new ActiveSubstanceUpdateDto("DoesNotExist", null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ActiveSubstanceUpdateDto> entity = new HttpEntity<>(updateDto, headers);

        ResponseEntity<Map> resp = restTemplate.exchange("/api/v1/active-substances/" + id, HttpMethod.PATCH, entity, Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Delete non-existing returns 404")
    void deleteNotFound() {
        UUID id = UUID.randomUUID();
        ResponseEntity<Map> resp = restTemplate.exchange("/api/v1/active-substances/" + id, HttpMethod.DELETE, null, Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ----------------- Additional cases below -----------------

    @Test
    @DisplayName("Update success: change name and synonyms")
    void updateSuccess() {
        String name = TEST_PREFIX + "UPD_SUCCESS";
        ActiveSubstanceCreateDto createDto = new ActiveSubstanceCreateDto(name, new String[]{"a"});
        ResponseEntity<ActiveSubstanceDto> createdResp = restTemplate.postForEntity("/api/v1/active-substances", createDto, ActiveSubstanceDto.class);
        assertThat(createdResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        UUID id = createdResp.getBody().id();

        ActiveSubstanceUpdateDto updateDto = new ActiveSubstanceUpdateDto(name + "_NEW", new String[]{"b","c"});
        HttpEntity<ActiveSubstanceUpdateDto> entity = new HttpEntity<>(updateDto, createJsonHeaders());

        ResponseEntity<ActiveSubstanceDto> updResp = restTemplate.exchange("/api/v1/active-substances/" + id, HttpMethod.PATCH, entity, ActiveSubstanceDto.class);
        assertThat(updResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ActiveSubstanceDto updated = updResp.getBody();
        assertThat(updated).isNotNull();
        assertThat(updated.name()).isEqualTo(name + "_NEW");
        assertThat(updated.synonyms()).containsExactly("b", "c");
    }

    @Test
    @DisplayName("Update validation fails when name blank")
    void updateValidationFails() {
        String name = TEST_PREFIX + "UPD_VALID";
        ActiveSubstanceCreateDto createDto = new ActiveSubstanceCreateDto(name, new String[]{"a"});
        ResponseEntity<ActiveSubstanceDto> createdResp = restTemplate.postForEntity("/api/v1/active-substances", createDto, ActiveSubstanceDto.class);
        assertThat(createdResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        UUID id = createdResp.getBody().id();

        ActiveSubstanceUpdateDto updateDto = new ActiveSubstanceUpdateDto("", null);
        HttpEntity<ActiveSubstanceUpdateDto> entity = new HttpEntity<>(updateDto, createJsonHeaders());

        ResponseEntity<Map> resp = restTemplate.exchange("/api/v1/active-substances/" + id, HttpMethod.PATCH, entity, Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().get("error")).isEqualTo("Bad Request");
    }

    @Test
    @DisplayName("Update duplicate name: current behaviour allows duplicates")
    void updateDuplicateName() {
        String nameA = TEST_PREFIX + "UPD_DUP_A";
        String nameB = TEST_PREFIX + "UPD_DUP_B";
        // create two
        ActiveSubstanceCreateDto a = new ActiveSubstanceCreateDto(nameA, null);
        ActiveSubstanceCreateDto b = new ActiveSubstanceCreateDto(nameB, null);
        UUID idA = restTemplate.postForEntity("/api/v1/active-substances", a, ActiveSubstanceDto.class).getBody().id();
        UUID idB = restTemplate.postForEntity("/api/v1/active-substances", b, ActiveSubstanceDto.class).getBody().id();

        // update B's name to A's name -> current service allows this, so expect OK
        ActiveSubstanceUpdateDto updateDto = new ActiveSubstanceUpdateDto(nameA, null);
        HttpEntity<ActiveSubstanceUpdateDto> entity = new HttpEntity<>(updateDto, createJsonHeaders());

        ResponseEntity<ActiveSubstanceDto> resp = restTemplate.exchange("/api/v1/active-substances/" + idB, HttpMethod.PATCH, entity, ActiveSubstanceDto.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ActiveSubstanceDto updated = resp.getBody();
        assertThat(updated).isNotNull();
        assertThat(updated.name()).isEqualTo(nameA);

        // Verify that both records now have the same name in the list
        ResponseEntity<ActiveSubstanceDto[]> listResp = restTemplate.getForEntity("/api/v1/active-substances", ActiveSubstanceDto[].class);
        ActiveSubstanceDto[] all = listResp.getBody();
        long count = List.of(all).stream().filter(s -> nameA.equals(s.name())).count();
        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Create with null synonyms succeeds (accept null array)")
    void createWithNullSynonyms() {
        String name = TEST_PREFIX + "NULL_SYNS";
        ActiveSubstanceCreateDto createDto = new ActiveSubstanceCreateDto(name, null);
        ResponseEntity<ActiveSubstanceDto> resp = restTemplate.postForEntity("/api/v1/active-substances", createDto, ActiveSubstanceDto.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ActiveSubstanceDto body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.name()).isEqualTo(name);
        // synonyms may be null or empty array depending on mapper; ensure no exception thrown
    }

    @Test
    @DisplayName("Create with invalid JSON payload returns 500 (product behaviour)")
    void createInvalidJson() {
        // send malformed JSON to trigger server error in current product behaviour
        HttpHeaders headers = createJsonHeaders();
        String invalidJson = "{ \"name\": }"; // syntactically invalid JSON
        HttpEntity<String> entity = new HttpEntity<>(invalidJson, headers);

        ResponseEntity<Map> resp = restTemplate.postForEntity("/api/v1/active-substances", entity, Map.class);
        // Current behaviour returns 500 due to parsing error bubbling up; assert current behaviour
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Patch with invalid JSON payload returns 500 (product behaviour)")
    void patchInvalidJson() {
        // create a valid entry first
        String name = TEST_PREFIX + "PATCH_INVALID";
        ActiveSubstanceCreateDto createDto = new ActiveSubstanceCreateDto(name, null);
        ResponseEntity<ActiveSubstanceDto> created = restTemplate.postForEntity("/api/v1/active-substances", createDto, ActiveSubstanceDto.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        UUID id = created.getBody().id();

        // send malformed JSON to PATCH
        HttpHeaders headers = createJsonHeaders();
        String invalidJson = "{ \"name\": }";
        HttpEntity<String> entity = new HttpEntity<>(invalidJson, headers);

        ResponseEntity<Map> resp = restTemplate.exchange("/api/v1/active-substances/" + id, HttpMethod.PATCH, entity, Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Create with unicode and emoji in name")
    void createWithUnicodeName() {
        String name = TEST_PREFIX + "UNICODE_µ_😀";
        ActiveSubstanceCreateDto createDto = new ActiveSubstanceCreateDto(name, new String[]{"syn"});
        ResponseEntity<ActiveSubstanceDto> resp = restTemplate.postForEntity("/api/v1/active-substances", createDto, ActiveSubstanceDto.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().name()).isEqualTo(name);
    }

    @Test
    @DisplayName("Create with empty synonyms array")
    void createWithEmptySynonymsArray() {
        String name = TEST_PREFIX + "EMPTY_SYNS";
        ActiveSubstanceCreateDto createDto = new ActiveSubstanceCreateDto(name, new String[]{});
        ResponseEntity<ActiveSubstanceDto> resp = restTemplate.postForEntity("/api/v1/active-substances", createDto, ActiveSubstanceDto.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ActiveSubstanceDto body = resp.getBody();
        assertThat(body).isNotNull();
        // synonyms may be empty array; ensure endpoint accepted it
        assertThat(body.name()).isEqualTo(name);
    }

    @Test
    @DisplayName("Create with SQL-like name (should be treated as data)")
    void createWithSqlLikeName() {
        String name = TEST_PREFIX + "SQL_'; DROP TABLE users; --";
        ActiveSubstanceCreateDto createDto = new ActiveSubstanceCreateDto(name, null);
        ResponseEntity<ActiveSubstanceDto> resp = restTemplate.postForEntity("/api/v1/active-substances", createDto, ActiveSubstanceDto.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().name()).isEqualTo(name);
    }

    @Test
    @DisplayName("Patch only synonyms (partial update)")
    void patchOnlySynonyms() {
        String name = TEST_PREFIX + "PATCH_ONLY_SYNS";
        ActiveSubstanceCreateDto createDto = new ActiveSubstanceCreateDto(name, new String[]{"a"});
        ActiveSubstanceDto created = restTemplate.postForEntity("/api/v1/active-substances", createDto, ActiveSubstanceDto.class).getBody();
        UUID id = created.id();

        // Patch only synonyms (name field null) — product behaviour validates name and returns 400
        String patchJson = "{ \"synonyms\": [\"x\", \"y\"] }";
        HttpEntity<String> entity = new HttpEntity<>(patchJson, createJsonHeaders());
        ResponseEntity<ActiveSubstanceDto> resp = restTemplate.exchange("/api/v1/active-substances/" + id, HttpMethod.PATCH, entity, ActiveSubstanceDto.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Patch only name (partial update)")
    void patchOnlyName() {
        String name = TEST_PREFIX + "PATCH_ONLY_NAME";
        ActiveSubstanceCreateDto createDto = new ActiveSubstanceCreateDto(name, new String[]{"a"});
        ActiveSubstanceDto created = restTemplate.postForEntity("/api/v1/active-substances", createDto, ActiveSubstanceDto.class).getBody();
        UUID id = created.id();

        String newName = name + "_NEW";
        String patchJson = "{ \"name\": \"" + newName + "\" }";
        HttpEntity<String> entity = new HttpEntity<>(patchJson, createJsonHeaders());
        ResponseEntity<ActiveSubstanceDto> resp = restTemplate.exchange("/api/v1/active-substances/" + id, HttpMethod.PATCH, entity, ActiveSubstanceDto.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ActiveSubstanceDto updated = resp.getBody();
        assertThat(updated).isNotNull();
        assertThat(updated.name()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Delete twice returns 404 on second delete")
    void deleteTwice() {
        String name = TEST_PREFIX + "DELETE_TWICE";
        ActiveSubstanceCreateDto createDto = new ActiveSubstanceCreateDto(name, null);
        ActiveSubstanceDto created = restTemplate.postForEntity("/api/v1/active-substances", createDto, ActiveSubstanceDto.class).getBody();
        UUID id = created.id();

        ResponseEntity<Void> firstDel = restTemplate.exchange("/api/v1/active-substances/" + id, HttpMethod.DELETE, null, Void.class);
        assertThat(firstDel.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<Map> secondDel = restTemplate.exchange("/api/v1/active-substances/" + id, HttpMethod.DELETE, null, Map.class);
        assertThat(secondDel.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("POST with wrong content-type (text/plain) returns 415 or error (product behaviour)")
    void postWithWrongContentType() {
        String name = TEST_PREFIX + "WRONG_CT";
        String json = "{ \"name\": \"" + name + "\" }";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        ResponseEntity<Map> resp = restTemplate.postForEntity("/api/v1/active-substances", entity, Map.class);
        // Product behaviour may return 415 or 500; assert it's an error (4xx or 5xx)
        assertThat(resp.getStatusCode().isError()).isTrue();
    }

    // helper to create JSON headers
    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

}
