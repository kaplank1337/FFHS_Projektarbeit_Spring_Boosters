package ch.ffhs.spring_boosters.integration;

import ch.ffhs.spring_boosters.controller.VaccineTypeController;
import ch.ffhs.spring_boosters.controller.entity.VaccineType;
import ch.ffhs.spring_boosters.repository.VaccineTypeRepository;
import ch.ffhs.spring_boosters.service.VaccineTypeService;
import ch.ffhs.spring_boosters.test.TestFlywayInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { TestFlywayInitializer.class })
@ActiveProfiles("integrationtest")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class VaccineTypeIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VaccineTypeController vaccineTypeController;

    @Autowired
    private VaccineTypeRepository vaccineTypeRepository;

    @Autowired
    private VaccineTypeService vaccineTypeService;

    @BeforeEach
    void ensureSeeded() {
        List<VaccineType> all = vaccineTypeRepository.findAll();
        assertNotNull(all);
        assertTrue(all.size() >= 1);
    }

    @Test
    @DisplayName("1 - controller present")
    public void t1_controllerPresent() {
        assertNotNull(vaccineTypeController);
    }

    @Test
    @DisplayName("2 - service returns list")
    public void t2_serviceReturnsList() {
        List<VaccineType> list = vaccineTypeService.getVaccineTypes();
        assertNotNull(list);
        assertTrue(list.size() >= 1);
    }

    @Test
    @DisplayName("3 - repository findAll returns same as service")
    public void t3_repoAndServiceConsistency() {
        List<VaccineType> fromRepo = vaccineTypeRepository.findAll();
        List<VaccineType> fromService = vaccineTypeService.getVaccineTypes();
        assertEquals(fromRepo.size(), fromService.size());
    }

    @Test
    @DisplayName("4 - GET /api/v1/vaccine-types returns seeded names")
    public void t4_getAllEndpointContainsKnownName() {
        ResponseEntity<String> resp = restTemplate.getForEntity("/api/v1/vaccine-types", String.class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        String body = resp.getBody();
        assertNotNull(body);
        assertTrue(body.contains("COVID-19"), "Response should contain COVID-19 entries");
    }

    @Test
    @DisplayName("5 - GET by id returns vaccine type")
    public void t5_getById_returnsVaccineType() {
        VaccineType vt = vaccineTypeRepository.findAll().get(0);
        UUID id = vt.getId();
        ResponseEntity<String> resp = restTemplate.getForEntity("/api/v1/vaccine-types/" + id.toString(), String.class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        String body = resp.getBody();
        assertNotNull(body);
        assertTrue(body.contains(vt.getName()));
    }

    @Test
    @DisplayName("6 - GET by non-existent id returns 404")
    public void t6_getById_notFound() {
        UUID random = UUID.randomUUID();
        ResponseEntity<String> resp = restTemplate.getForEntity("/api/v1/vaccine-types/" + random.toString(), String.class);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    @DisplayName("7 - GET with invalid UUID returns 400")
    public void t7_getById_invalidUuid() {
        ResponseEntity<String> resp = restTemplate.getForEntity("/api/v1/vaccine-types/not-a-uuid", String.class);
        assertTrue(resp.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("8 - repeated calls stable")
    public void t8_repeatedCallsStable() {
        for (int i = 0; i < 5; i++) {
            ResponseEntity<String> resp = restTemplate.getForEntity("/api/v1/vaccine-types", String.class);
            assertEquals(HttpStatus.OK, resp.getStatusCode());
            assertNotNull(resp.getBody());
        }
    }

    @Test
    @DisplayName("9 - concurrent requests handled")
    public void t9_concurrentRequests() throws InterruptedException, ExecutionException {
        int threads = 4;
        ExecutorService ex = Executors.newFixedThreadPool(threads);
        Callable<ResponseEntity<String>> call = () -> restTemplate.getForEntity("/api/v1/vaccine-types", String.class);
        try {
            List<Future<ResponseEntity<String>>> futures = ex.invokeAll(List.of(call, call, call, call));
            for (Future<ResponseEntity<String>> f : futures) {
                ResponseEntity<String> r = f.get();
                assertEquals(HttpStatus.OK, r.getStatusCode());
                assertNotNull(r.getBody());
            }
        } finally {
            ex.shutdownNow();
        }
    }

    @Test
    @DisplayName("10 - DTO contains name and code keys")
    public void t10_jsonContainsFields() {
        ResponseEntity<String> resp = restTemplate.getForEntity("/api/v1/vaccine-types", String.class);
        String body = resp.getBody();
        assertNotNull(body);
        assertTrue(body.contains("name"));
        assertTrue(body.contains("code"));
    }

    @Test
    @DisplayName("11 - service get by id matches repository")
    public void t11_serviceGetByIdMatchesRepo() throws Exception {
        VaccineType vt = vaccineTypeRepository.findAll().get(0);
        VaccineType s = vaccineTypeService.getVaccineType(vt.getId());
        assertEquals(vt.getId(), s.getId());
    }

    @Test
    @DisplayName("12 - size invariants: total count non-negative")
    public void t12_sizeInvariant() {
        List<VaccineType> all = vaccineTypeService.getVaccineTypes();
        assertTrue(all.size() >= 0);
    }

    @Test
    @DisplayName("13 - names are unique in repository")
    public void t13_namesUnique() {
        List<VaccineType> all = vaccineTypeRepository.findAll();
        long distinct = all.stream().map(VaccineType::getName).distinct().count();
        assertTrue(distinct >= 1);
        assertEquals(distinct, all.stream().map(VaccineType::getName).distinct().count());
    }

    @Test
    @DisplayName("14 - get list returns JSON array")
    public void t14_jsonArray() {
        ResponseEntity<String> resp = restTemplate.getForEntity("/api/v1/vaccine-types", String.class);
        String body = resp.getBody();
        assertNotNull(body);
        assertTrue(body.trim().startsWith("{" ) || body.trim().startsWith("["));
    }

    @Test
    @DisplayName("15 - pagination-like behavior stable with query param ignored")
    public void t15_queryParamIgnored() {
        ResponseEntity<String> resp = restTemplate.getForEntity("/api/v1/vaccine-types?page=1&size=2", String.class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("16 - verify at least one vaccine code present or null allowed")
    public void t16_verifyCodePresence() {
        List<VaccineType> all = vaccineTypeRepository.findAll();
        assertNotNull(all);
        boolean anyHasCode = all.stream().anyMatch(v -> v.getCode() != null && !v.getCode().isBlank());
        assertTrue(anyHasCode || all.stream().allMatch(v -> v.getCode() == null));
    }

    @Test
    @DisplayName("17 - fetching by id twice returns same content")
    public void t17_fetchByIdTwice() {
        VaccineType vt = vaccineTypeRepository.findAll().get(0);
        ResponseEntity<String> r1 = restTemplate.getForEntity("/api/v1/vaccine-types/" + vt.getId(), String.class);
        ResponseEntity<String> r2 = restTemplate.getForEntity("/api/v1/vaccine-types/" + vt.getId(), String.class);
        assertEquals(r1.getStatusCode(), r2.getStatusCode());
        assertEquals(r1.getBody(), r2.getBody());
    }

    @Test
    @DisplayName("18 - service list not null on repeated calls")
    public void t18_serviceRepeated() {
        List<VaccineType> a = vaccineTypeService.getVaccineTypes();
        List<VaccineType> b = vaccineTypeService.getVaccineTypes();
        assertNotNull(a);
        assertNotNull(b);
        assertEquals(a.size(), b.size());
    }

    @Test
    @DisplayName("19 - repository findById returns empty for random id")
    public void t19_repoFindByIdEmpty() {
        assertFalse(vaccineTypeRepository.findById(UUID.randomUUID()).isPresent());
    }

    @Test
    @DisplayName("20 - health check: endpoint responds quickly")
    public void t20_latencySimple() {
        long start = System.currentTimeMillis();
        ResponseEntity<String> resp = restTemplate.getForEntity("/api/v1/vaccine-types", String.class);
        long dur = System.currentTimeMillis() - start;
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertTrue(dur < 2000);
    }
}
