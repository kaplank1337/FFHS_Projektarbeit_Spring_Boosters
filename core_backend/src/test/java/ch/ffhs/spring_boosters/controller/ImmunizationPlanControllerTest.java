package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.controller.dto.ImmunizationPlanCreateDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationPlanDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationPlanUpdateDto;
import ch.ffhs.spring_boosters.controller.entity.ImmunizationPlan;
import ch.ffhs.spring_boosters.controller.mapper.ImmunizationPlanMapper;
import ch.ffhs.spring_boosters.service.ImmunizationPlanService;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationPlanAlreadyExistsException;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationPlanNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ImmunizationPlanControllerTest {

    @Mock
    private ImmunizationPlanService immunizationPlanService;

    @Mock
    private ImmunizationPlanMapper immunizationPlanMapper;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ImmunizationPlanController controller = new ImmunizationPlanController(immunizationPlanService, immunizationPlanMapper);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // helpers
    private ImmunizationPlan sampleEntity(UUID id, String name, UUID vaccineTypeId, UUID ageCategoryId) {
        ImmunizationPlan e = new ImmunizationPlan();
        e.setId(id);
        e.setName(name);
        e.setVaccineTypeId(vaccineTypeId);
        e.setAgeCategoryId(ageCategoryId);
        return e;
    }

    private ImmunizationPlanDto sampleDto(UUID id, String name, UUID vaccineTypeId, UUID ageCategoryId) {
        OffsetDateTime now = OffsetDateTime.now();
        return new ImmunizationPlanDto(id, name, vaccineTypeId, ageCategoryId, now, now);
    }

    private ImmunizationPlanCreateDto sampleCreateDto(String name, UUID vaccineTypeId, UUID ageCategoryId) {
        return new ImmunizationPlanCreateDto(name, vaccineTypeId, ageCategoryId);
    }

    private ImmunizationPlanUpdateDto sampleUpdateDto(String name, UUID vaccineTypeId, UUID ageCategoryId) {
        return new ImmunizationPlanUpdateDto(name, vaccineTypeId, ageCategoryId);
    }

    @Test
    void getAllImmunizationPlans_returnsList() throws Exception {
        UUID id = UUID.randomUUID();
        UUID vt = UUID.randomUUID();
        UUID ac = UUID.randomUUID();
        ImmunizationPlan entity = sampleEntity(id, "Plan A", vt, ac);
        ImmunizationPlanDto dto = sampleDto(id, "Plan A", vt, ac);

        when(immunizationPlanService.getAllImmunizationPlans()).thenReturn(List.of(entity));
        when(immunizationPlanMapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/immunization-plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(id.toString())))
                .andExpect(jsonPath("$[0].name", is("Plan A")));
    }

    @Test
    void getById_success() throws Exception {
        UUID id = UUID.randomUUID();
        UUID vt = UUID.randomUUID();
        UUID ac = UUID.randomUUID();
        ImmunizationPlan entity = sampleEntity(id, "Plan B", vt, ac);
        ImmunizationPlanDto dto = sampleDto(id, "Plan B", vt, ac);

        when(immunizationPlanService.getImmunizationPlanById(id)).thenReturn(entity);
        when(immunizationPlanMapper.toDto(entity)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/immunization-plans/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Plan B")));
    }

    @Test
    void getById_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(immunizationPlanService.getImmunizationPlanById(id)).thenThrow(new ImmunizationPlanNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/immunization-plans/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("Not found")));
    }

    @Test
    void createImmunizationPlan_success() throws Exception {
        UUID id = UUID.randomUUID();
        UUID vt = UUID.randomUUID();
        UUID ac = UUID.randomUUID();
        ImmunizationPlanCreateDto createDto = sampleCreateDto("Test Impfplan Säugling", vt, ac);

        ImmunizationPlan entityFromDto = sampleEntity(null, "Test Impfplan Säugling", vt, ac);
        ImmunizationPlan createdEntity = sampleEntity(id, "Test Impfplan Säugling", vt, ac);
        ImmunizationPlanDto responseDto = sampleDto(id, "Test Impfplan Säugling", vt, ac);

        when(immunizationPlanMapper.fromCreateDto(any(ImmunizationPlanCreateDto.class))).thenReturn(entityFromDto);
        when(immunizationPlanService.createImmunizationPlan(entityFromDto)).thenReturn(createdEntity);
        when(immunizationPlanMapper.toDto(createdEntity)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/immunization-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Test Impfplan Säugling")))
                .andExpect(jsonPath("$.vaccineTypeId", is(vt.toString())))
                .andExpect(jsonPath("$.ageCategoryId", is(ac.toString())));
    }

    @Test
    void createImmunizationPlan_alreadyExists() throws Exception {
        UUID vt = UUID.randomUUID();
        UUID ac = UUID.randomUUID();
        ImmunizationPlanCreateDto createDto = sampleCreateDto("Duplicate", vt, ac);
        ImmunizationPlan entityFromDto = sampleEntity(null, "Duplicate", vt, ac);

        when(immunizationPlanMapper.fromCreateDto(any(ImmunizationPlanCreateDto.class))).thenReturn(entityFromDto);
        when(immunizationPlanService.createImmunizationPlan(entityFromDto))
                .thenThrow(new ImmunizationPlanAlreadyExistsException("Already exists"));

        mockMvc.perform(post("/api/v1/immunization-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", containsString("Already exists")));
    }

    @Test
    void createImmunizationPlan_validationFails_missingFields() throws Exception {
        // missing vaccineTypeId and ageCategoryId -> validation should fail
        ImmunizationPlanCreateDto createDto = new ImmunizationPlanCreateDto("Name", null, null);

        mockMvc.perform(post("/api/v1/immunization-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateImmunizationPlan_success() throws Exception {
        UUID id = UUID.randomUUID();
        UUID vt = UUID.randomUUID();
        UUID ac = UUID.randomUUID();
        ImmunizationPlanUpdateDto updateDto = sampleUpdateDto("Updated", vt, ac);
        ImmunizationPlan entityFromDto = sampleEntity(null, "Updated", vt, ac);
        ImmunizationPlan updatedEntity = sampleEntity(id, "Updated", vt, ac);
        ImmunizationPlanDto responseDto = sampleDto(id, "Updated", vt, ac);

        when(immunizationPlanMapper.fromUpdateDto(any(ImmunizationPlanUpdateDto.class))).thenReturn(entityFromDto);
        when(immunizationPlanService.updateImmunizationPlan(eq(id), eq(entityFromDto))).thenReturn(updatedEntity);
        when(immunizationPlanMapper.toDto(updatedEntity)).thenReturn(responseDto);

        mockMvc.perform(patch("/api/v1/immunization-plans/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Updated")));
    }

    @Test
    void updateImmunizationPlan_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        UUID vt = UUID.randomUUID();
        UUID ac = UUID.randomUUID();
        ImmunizationPlanUpdateDto updateDto = sampleUpdateDto("Nope", vt, ac);
        ImmunizationPlan entityFromDto = sampleEntity(null, "Nope", vt, ac);

        when(immunizationPlanMapper.fromUpdateDto(any(ImmunizationPlanUpdateDto.class))).thenReturn(entityFromDto);
        when(immunizationPlanService.updateImmunizationPlan(eq(id), eq(entityFromDto)))
                .thenThrow(new ImmunizationPlanNotFoundException("Not found"));

        mockMvc.perform(patch("/api/v1/immunization-plans/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    @Test
    void updateImmunizationPlan_alreadyExists() throws Exception {
        UUID id = UUID.randomUUID();
        UUID vt = UUID.randomUUID();
        UUID ac = UUID.randomUUID();
        ImmunizationPlanUpdateDto updateDto = sampleUpdateDto("Exists", vt, ac);
        ImmunizationPlan entityFromDto = sampleEntity(null, "Exists", vt, ac);

        when(immunizationPlanMapper.fromUpdateDto(any(ImmunizationPlanUpdateDto.class))).thenReturn(entityFromDto);
        when(immunizationPlanService.updateImmunizationPlan(eq(id), eq(entityFromDto)))
                .thenThrow(new ImmunizationPlanAlreadyExistsException("Already exists"));

        mockMvc.perform(patch("/api/v1/immunization-plans/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")));
    }

    @Test
    void updateImmunizationPlan_validationFails() throws Exception {
        UUID id = UUID.randomUUID();
        ImmunizationPlanUpdateDto updateDto = new ImmunizationPlanUpdateDto("", null, null);

        mockMvc.perform(patch("/api/v1/immunization-plans/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteImmunizationPlan_success() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(immunizationPlanService).deleteImmunizationPlan(id);

        mockMvc.perform(delete("/api/v1/immunization-plans/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteImmunizationPlan_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new ImmunizationPlanNotFoundException("Not found")).when(immunizationPlanService).deleteImmunizationPlan(id);

        mockMvc.perform(delete("/api/v1/immunization-plans/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    @Test
    void getByVaccineType_returnsList() throws Exception {
        UUID vt = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        UUID ac = UUID.randomUUID();
        ImmunizationPlan entity = sampleEntity(id, "Plan VT", vt, ac);
        ImmunizationPlanDto dto = sampleDto(id, "Plan VT", vt, ac);

        when(immunizationPlanService.getImmunizationPlansByVaccineType(vt)).thenReturn(List.of(entity));
        when(immunizationPlanMapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/immunization-plans/by-vaccine-type/" + vt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].vaccineTypeId", is(vt.toString())));
    }

    @Test
    void getByAgeCategory_returnsList() throws Exception {
        UUID ac = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        UUID vt = UUID.randomUUID();
        ImmunizationPlan entity = sampleEntity(id, "Plan AC", vt, ac);
        ImmunizationPlanDto dto = sampleDto(id, "Plan AC", vt, ac);

        when(immunizationPlanService.getImmunizationPlansByAgeCategory(ac)).thenReturn(List.of(entity));
        when(immunizationPlanMapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/immunization-plans/by-age-category/" + ac))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].ageCategoryId", is(ac.toString())));
    }
}

