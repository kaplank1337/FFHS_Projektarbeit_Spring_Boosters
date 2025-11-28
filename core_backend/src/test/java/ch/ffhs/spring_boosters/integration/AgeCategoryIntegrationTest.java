package ch.ffhs.spring_boosters.integration;

import ch.ffhs.spring_boosters.controller.dto.AgeCategoryCreateDto;
import ch.ffhs.spring_boosters.controller.dto.AgeCategoryDto;
import ch.ffhs.spring_boosters.controller.dto.AgeCategoryUpdateDto;
import ch.ffhs.spring_boosters.controller.AgeCategoryController;
import ch.ffhs.spring_boosters.controller.mapper.AgeCategoryMapper;
import ch.ffhs.spring_boosters.repository.AgeCategoryRepository;
import ch.ffhs.spring_boosters.service.AgeCategoryService;
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
public class AgeCategoryIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AgeCategoryController ageCategoryController;

    @Autowired
    private AgeCategoryRepository ageCategoryRepository;

    @Autowired
    private AgeCategoryService ageCategoryService;

    @Autowired
    private AgeCategoryMapper ageCategoryMapper;

    private final String TEST_PREFIX = "IT_AGE_" + UUID.randomUUID().toString().substring(0, 8) + "_";

    @BeforeEach
    void beforeEach() {
        // keep DB seed; tests use unique names to avoid collisions
    }

    @Test
    @DisplayName("Full lifecycle: create -> get -> list -> delete (happy path)")
    void lifecycleCreateGetListDelete() {
        String name = TEST_PREFIX + "ADULTS";
        AgeCategoryCreateDto createDto = new AgeCategoryCreateDto(name, 3651, null);

        // Create
        ResponseEntity<AgeCategoryDto> createResp = restTemplate.postForEntity("/api/v1/age-categories", createDto, AgeCategoryDto.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        AgeCategoryDto created = createResp.getBody();
        assertThat(created).isNotNull();
        assertThat(created.name()).isEqualTo(name);
        UUID id = created.id();

        // Get by id
        ResponseEntity<AgeCategoryDto> getResp = restTemplate.getForEntity("/api/v1/age-categories/" + id, AgeCategoryDto.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResp.getBody()).isNotNull();
        assertThat(getResp.getBody().name()).isEqualTo(name);

        // List
        ResponseEntity<AgeCategoryDto[]> listResp = restTemplate.getForEntity("/api/v1/age-categories", AgeCategoryDto[].class);
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        AgeCategoryDto[] all = listResp.getBody();
        assertThat(all).isNotEmpty();
        boolean found = List.of(all).stream().anyMatch(a -> name.equals(a.name()));
        assertThat(found).isTrue();

        // Delete
        ResponseEntity<Void> delResp = restTemplate.exchange("/api/v1/age-categories/" + id, HttpMethod.DELETE, null, Void.class);
        assertThat(delResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // After delete
        ResponseEntity<Map> getAfterDel = restTemplate.getForEntity("/api/v1/age-categories/" + id, Map.class);
        assertThat(getAfterDel.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Create fails when name blank or ageMinDays null")
    void createValidationFails() {
        AgeCategoryCreateDto missingName = new AgeCategoryCreateDto("", 0, 10);
        ResponseEntity<Map> resp1 = restTemplate.postForEntity("/api/v1/age-categories", missingName, Map.class);
        assertThat(resp1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        AgeCategoryCreateDto missingMin = new AgeCategoryCreateDto("NoMin", null, 10);
        ResponseEntity<Map> resp2 = restTemplate.postForEntity("/api/v1/age-categories", missingMin, Map.class);
        assertThat(resp2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Create duplicate name returns 400")
    void createDuplicateReturnsBadRequest() {
        String name = TEST_PREFIX + "DUP";
        AgeCategoryCreateDto dto = new AgeCategoryCreateDto(name, 0, 10);
        ResponseEntity<AgeCategoryDto> first = restTemplate.postForEntity("/api/v1/age-categories", dto, AgeCategoryDto.class);
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // second create same name -> should return 400 (AgeCategoryAlreadyExistsException handled by controller)
        ResponseEntity<Map> second = restTemplate.postForEntity("/api/v1/age-categories", dto, Map.class);
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Get non-existing id returns 404")
    void getNotFound() {
        UUID id = UUID.randomUUID();
        ResponseEntity<Map> resp = restTemplate.getForEntity("/api/v1/age-categories/" + id, Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Update non-existing returns 404")
    void updateNotFound() {
        UUID id = UUID.randomUUID();
        AgeCategoryUpdateDto updateDto = new AgeCategoryUpdateDto("DoesNotExist", 10, 20);
        HttpEntity<AgeCategoryUpdateDto> entity = new HttpEntity<>(updateDto, createJsonHeaders());
        ResponseEntity<Map> resp = restTemplate.exchange("/api/v1/age-categories/" + id, HttpMethod.PATCH, entity, Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Update success: change name and age range")
    void updateSuccess() {
        String name = TEST_PREFIX + "UPD_SUCCESS";
        AgeCategoryCreateDto createDto = new AgeCategoryCreateDto(name, 0, 100);
        AgeCategoryDto created = restTemplate.postForEntity("/api/v1/age-categories", createDto, AgeCategoryDto.class).getBody();
        UUID id = created.id();

        AgeCategoryUpdateDto updateDto = new AgeCategoryUpdateDto(name + "_NEW", 1, 200);
        HttpEntity<AgeCategoryUpdateDto> entity = new HttpEntity<>(updateDto, createJsonHeaders());
        ResponseEntity<AgeCategoryDto> resp = restTemplate.exchange("/api/v1/age-categories/" + id, HttpMethod.PATCH, entity, AgeCategoryDto.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        AgeCategoryDto updated = resp.getBody();
        assertThat(updated).isNotNull();
        assertThat(updated.name()).isEqualTo(name + "_NEW");
        assertThat(updated.ageMinDays()).isEqualTo(1);
        assertThat(updated.ageMaxDays()).isEqualTo(200);
    }

    @Test
    @DisplayName("Update validation fails when name blank or min null")
    void updateValidationFails() {
        String name = TEST_PREFIX + "UPD_VALID";
        AgeCategoryCreateDto createDto = new AgeCategoryCreateDto(name, 0, 10);
        AgeCategoryDto created = restTemplate.postForEntity("/api/v1/age-categories", createDto, AgeCategoryDto.class).getBody();
        UUID id = created.id();

        AgeCategoryUpdateDto bad1 = new AgeCategoryUpdateDto("", 0, 10);
        ResponseEntity<Map> r1 = restTemplate.exchange("/api/v1/age-categories/" + id, HttpMethod.PATCH, new HttpEntity<>(bad1, createJsonHeaders()), Map.class);
        assertThat(r1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        AgeCategoryUpdateDto bad2 = new AgeCategoryUpdateDto(null, null, 5);
        ResponseEntity<Map> r2 = restTemplate.exchange("/api/v1/age-categories/" + id, HttpMethod.PATCH, new HttpEntity<>(bad2, createJsonHeaders()), Map.class);
        // depending on validation, missing min may be rejected -> expect BAD_REQUEST
        assertThat(r2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Patch only name (partial update)")
    void patchOnlyName() {
        String name = TEST_PREFIX + "PATCH_NAME";
        AgeCategoryCreateDto createDto = new AgeCategoryCreateDto(name, 0, 10);
        AgeCategoryDto created = restTemplate.postForEntity("/api/v1/age-categories", createDto, AgeCategoryDto.class).getBody();
        UUID id = created.id();

        String newName = name + "_NEW";
        String patchJson = "{ \"name\": \"" + newName + "\" }";
        ResponseEntity<AgeCategoryDto> resp = restTemplate.exchange("/api/v1/age-categories/" + id, HttpMethod.PATCH, new HttpEntity<>(patchJson, createJsonHeaders()), AgeCategoryDto.class);
        // Product behaviour validates the update DTO — partial payload missing required fields results in BAD_REQUEST
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Patch only min/max (partial update)")
    void patchOnlyRange() {
        String name = TEST_PREFIX + "PATCH_RANGE";
        AgeCategoryCreateDto createDto = new AgeCategoryCreateDto(name, 100, 200);
        AgeCategoryDto created = restTemplate.postForEntity("/api/v1/age-categories", createDto, AgeCategoryDto.class).getBody();
        UUID id = created.id();

        String patchJson = "{ \"ageMinDays\": 50, \"ageMaxDays\": 150 }";
        ResponseEntity<AgeCategoryDto> resp = restTemplate.exchange("/api/v1/age-categories/" + id, HttpMethod.PATCH, new HttpEntity<>(patchJson, createJsonHeaders()), AgeCategoryDto.class);
        // Product behaviour: DTO validation enforces presence of name and min; partial payload -> BAD_REQUEST
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Create with unicode name")
    void createWithUnicodeName() {
        String name = TEST_PREFIX + "UNICODE_ä_😀";
        AgeCategoryCreateDto dto = new AgeCategoryCreateDto(name, 0, 10);
        ResponseEntity<AgeCategoryDto> resp = restTemplate.postForEntity("/api/v1/age-categories", dto, AgeCategoryDto.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().name()).isEqualTo(name);
    }

    @Test
    @DisplayName("Create with SQL-like name is treated as data")
    void createWithSqlLikeName() {
        String name = TEST_PREFIX + "SQL_'; DROP TABLE users; --";
        AgeCategoryCreateDto dto = new AgeCategoryCreateDto(name, 0, 1);
        ResponseEntity<AgeCategoryDto> resp = restTemplate.postForEntity("/api/v1/age-categories", dto, AgeCategoryDto.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().name()).isEqualTo(name);
    }

    @Test
    @DisplayName("Create with invalid JSON returns 500 (product behaviour)")
    void createInvalidJson() {
        HttpHeaders headers = createJsonHeaders();
        String invalidJson = "{ \"name\": }";
        ResponseEntity<Map> resp = restTemplate.postForEntity("/api/v1/age-categories", new HttpEntity<>(invalidJson, headers), Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("POST wrong content-type returns error")
    void postWithWrongContentType() {
        String name = TEST_PREFIX + "WRONG_CT";
        String json = "{ \"name\": \"" + name + "\", \"ageMinDays\": 0 }";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        ResponseEntity<Map> resp = restTemplate.postForEntity("/api/v1/age-categories", new HttpEntity<>(json, headers), Map.class);
        assertThat(resp.getStatusCode().isError()).isTrue();
    }

    @Test
    @DisplayName("Delete twice returns 404 on second delete")
    void deleteTwice() {
        String name = TEST_PREFIX + "DELETE_TWICE";
        AgeCategoryCreateDto createDto = new AgeCategoryCreateDto(name, 0, 1);
        AgeCategoryDto created = restTemplate.postForEntity("/api/v1/age-categories", createDto, AgeCategoryDto.class).getBody();
        UUID id = created.id();

        ResponseEntity<Void> first = restTemplate.exchange("/api/v1/age-categories/" + id, HttpMethod.DELETE, null, Void.class);
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<Map> second = restTemplate.exchange("/api/v1/age-categories/" + id, HttpMethod.DELETE, null, Map.class);
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // helper
    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
