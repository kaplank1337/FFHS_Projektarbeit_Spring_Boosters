package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.controller.dto.AgeCategoryCreateDto;
import ch.ffhs.spring_boosters.controller.dto.AgeCategoryDto;
import ch.ffhs.spring_boosters.controller.dto.AgeCategoryUpdateDto;
import ch.ffhs.spring_boosters.controller.entity.AgeCategory;
import ch.ffhs.spring_boosters.controller.mapper.AgeCategoryMapper;
import ch.ffhs.spring_boosters.service.AgeCategoryService;
import ch.ffhs.spring_boosters.service.Exception.AgeCategoryAlreadyExistsException;
import ch.ffhs.spring_boosters.service.Exception.AgeCategoryNotFoundException;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AgeCategoryControllerTest {

    @Mock
    private AgeCategoryService ageCategoryService;

    @Mock
    private AgeCategoryMapper ageCategoryMapper;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        AgeCategoryController controller = new AgeCategoryController(ageCategoryService, ageCategoryMapper);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // helpers
    private AgeCategory sampleEntity(UUID id, String name, Integer minDays, Integer maxDays) {
        AgeCategory e = new AgeCategory();
        e.setId(id);
        e.setName(name);
        e.setAgeMinDays(minDays);
        e.setAgeMaxDays(maxDays);
        return e;
    }

    private AgeCategoryDto sampleDto(UUID id, String name, Integer minDays, Integer maxDays) {
        OffsetDateTime now = OffsetDateTime.now();
        return new AgeCategoryDto(id, name, minDays, maxDays, now, now);
    }

    private AgeCategoryCreateDto sampleCreateDto(String name, Integer minDays, Integer maxDays) {
        return new AgeCategoryCreateDto(name, minDays, maxDays);
    }

    private AgeCategoryUpdateDto sampleUpdateDto(String name, Integer minDays, Integer maxDays) {
        return new AgeCategoryUpdateDto(name, minDays, maxDays);
    }

    @Test
    void getAllAgeCategories_returnsList() throws Exception {
        UUID id = UUID.randomUUID();
        AgeCategory entity = sampleEntity(id, "Säugling", 0, 365);
        AgeCategoryDto dto = sampleDto(id, "Säugling", 0, 365);

        when(ageCategoryService.getAllAgeCategories()).thenReturn(List.of(entity));
        when(ageCategoryMapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/age-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(id.toString())))
                .andExpect(jsonPath("$[0].name", is("Säugling")));
    }

    @Test
    void getById_success() throws Exception {
        UUID id = UUID.randomUUID();
        AgeCategory entity = sampleEntity(id, "Kind", 366, 3650);
        AgeCategoryDto dto = sampleDto(id, "Kind", 366, 3650);

        when(ageCategoryService.getAgeCategoryById(id)).thenReturn(entity);
        when(ageCategoryMapper.toDto(entity)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/age-categories/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Kind")));
    }

    @Test
    void getById_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(ageCategoryService.getAgeCategoryById(id)).thenThrow(new AgeCategoryNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/age-categories/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("Not found")));
    }

    @Test
    void createAgeCategory_success() throws Exception {
        UUID id = UUID.randomUUID();
        AgeCategoryCreateDto createDto = sampleCreateDto("Erwachsene", 3651, null);

        AgeCategory entityFromDto = sampleEntity(null, "Erwachsene", 3651, null);
        AgeCategory createdEntity = sampleEntity(id, "Erwachsene", 3651, null);
        AgeCategoryDto responseDto = sampleDto(id, "Erwachsene", 3651, null);

        when(ageCategoryMapper.fromCreateDto(any(AgeCategoryCreateDto.class))).thenReturn(entityFromDto);
        when(ageCategoryService.createAgeCategory(entityFromDto)).thenReturn(createdEntity);
        when(ageCategoryMapper.toDto(createdEntity)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/age-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Erwachsene")));
    }

    @Test
    void createAgeCategory_alreadyExists() throws Exception {
        AgeCategoryCreateDto createDto = sampleCreateDto("Duplicate", 0, 10);
        AgeCategory entityFromDto = sampleEntity(null, "Duplicate", 0, 10);

        when(ageCategoryMapper.fromCreateDto(any(AgeCategoryCreateDto.class))).thenReturn(entityFromDto);
        when(ageCategoryService.createAgeCategory(entityFromDto))
                .thenThrow(new AgeCategoryAlreadyExistsException("Already exists"));

        mockMvc.perform(post("/api/v1/age-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", containsString("Already exists")));
    }

    @Test
    void createAgeCategory_validationFails_emptyName() throws Exception {
        AgeCategoryCreateDto createDto = sampleCreateDto("", 0, 10);

        mockMvc.perform(post("/api/v1/age-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAgeCategory_validationFails_missingMinDays() throws Exception {
        // ageMinDays is @NotNull -> null should trigger validation
        AgeCategoryCreateDto createDto = sampleCreateDto("NoMin", null, 10);

        mockMvc.perform(post("/api/v1/age-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAgeCategory_success() throws Exception {
        UUID id = UUID.randomUUID();
        AgeCategoryUpdateDto updateDto = sampleUpdateDto("Updated", 1, 100);
        AgeCategory entityFromDto = sampleEntity(null, "Updated", 1, 100);
        AgeCategory updatedEntity = sampleEntity(id, "Updated", 1, 100);
        AgeCategoryDto responseDto = sampleDto(id, "Updated", 1, 100);

        when(ageCategoryMapper.fromUpdateDto(any(AgeCategoryUpdateDto.class))).thenReturn(entityFromDto);
        when(ageCategoryService.updateAgeCategory(eq(id), eq(entityFromDto))).thenReturn(updatedEntity);
        when(ageCategoryMapper.toDto(updatedEntity)).thenReturn(responseDto);

        mockMvc.perform(patch("/api/v1/age-categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Updated")));
    }

    @Test
    void updateAgeCategory_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        AgeCategoryUpdateDto updateDto = sampleUpdateDto("Nope", 0, 10);
        AgeCategory entityFromDto = sampleEntity(null, "Nope", 0, 10);

        when(ageCategoryMapper.fromUpdateDto(any(AgeCategoryUpdateDto.class))).thenReturn(entityFromDto);
        when(ageCategoryService.updateAgeCategory(eq(id), eq(entityFromDto)))
                .thenThrow(new AgeCategoryNotFoundException("Not found"));

        mockMvc.perform(patch("/api/v1/age-categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    @Test
    void updateAgeCategory_alreadyExists() throws Exception {
        UUID id = UUID.randomUUID();
        AgeCategoryUpdateDto updateDto = sampleUpdateDto("Exists", 0, 10);
        AgeCategory entityFromDto = sampleEntity(null, "Exists", 0, 10);

        when(ageCategoryMapper.fromUpdateDto(any(AgeCategoryUpdateDto.class))).thenReturn(entityFromDto);
        when(ageCategoryService.updateAgeCategory(eq(id), eq(entityFromDto)))
                .thenThrow(new AgeCategoryAlreadyExistsException("Already exists"));

        mockMvc.perform(patch("/api/v1/age-categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")));
    }

    @Test
    void updateAgeCategory_validationFails() throws Exception {
        UUID id = UUID.randomUUID();
        AgeCategoryUpdateDto updateDto = sampleUpdateDto("", null, null);

        mockMvc.perform(patch("/api/v1/age-categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAgeCategory_success() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(ageCategoryService).deleteAgeCategory(id);

        mockMvc.perform(delete("/api/v1/age-categories/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAgeCategory_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new AgeCategoryNotFoundException("Not found")).when(ageCategoryService).deleteAgeCategory(id);

        mockMvc.perform(delete("/api/v1/age-categories/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }
}

