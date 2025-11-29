package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.controller.dto.ActiveSubstanceCreateDto;
import ch.ffhs.spring_boosters.controller.dto.ActiveSubstanceDto;
import ch.ffhs.spring_boosters.controller.dto.ActiveSubstanceUpdateDto;
import ch.ffhs.spring_boosters.controller.entity.ActiveSubstance;
import ch.ffhs.spring_boosters.controller.mapper.ActiveSubstanceMapper;
import ch.ffhs.spring_boosters.service.ActiveSubstanceService;
import ch.ffhs.spring_boosters.service.Exception.ActiveSubstanceAlreadyExistsException;
import ch.ffhs.spring_boosters.service.Exception.ActiveSubstanceNotFoundException;
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
class ActiveSubstanceControllerTest {

    @Mock
    private ActiveSubstanceService activeSubstanceService;

    @Mock
    private ActiveSubstanceMapper activeSubstanceMapper;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ActiveSubstanceController controller = new ActiveSubstanceController(activeSubstanceService, activeSubstanceMapper);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private ActiveSubstance sampleEntity(UUID id, String name) {
        ActiveSubstance e = new ActiveSubstance();
        e.setId(id);
        e.setName(name);
        e.setSynonyms(new String[]{});
        return e;
    }

    private ActiveSubstanceDto sampleDto(UUID id, String name) {
        OffsetDateTime now = OffsetDateTime.now();
        return new ActiveSubstanceDto(id, name, new String[]{}, now, now);
    }

    private ActiveSubstanceCreateDto sampleCreateDto(String name) {
        return new ActiveSubstanceCreateDto(name, new String[]{});
    }

    private ActiveSubstanceUpdateDto sampleUpdateDto(String name) {
        return new ActiveSubstanceUpdateDto(name, new String[]{});
    }

    @Test
    void getAllActiveSubstances_returnsList() throws Exception {
        UUID id = UUID.randomUUID();
        ActiveSubstance entity = sampleEntity(id, "Substance A");
        ActiveSubstanceDto dto = sampleDto(id, "Substance A");

        when(activeSubstanceService.getAllActiveSubstances()).thenReturn(List.of(entity));
        when(activeSubstanceMapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/active-substances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(id.toString())))
                .andExpect(jsonPath("$[0].name", is("Substance A")));
    }

    @Test
    void getById_success() throws Exception {
        UUID id = UUID.randomUUID();
        ActiveSubstance entity = sampleEntity(id, "Substance B");
        ActiveSubstanceDto dto = sampleDto(id, "Substance B");

        when(activeSubstanceService.getActiveSubstanceById(id)).thenReturn(entity);
        when(activeSubstanceMapper.toDto(entity)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/active-substances/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Substance B")));
    }

    @Test
    void getById_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(activeSubstanceService.getActiveSubstanceById(id)).thenThrow(new ActiveSubstanceNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/active-substances/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("Not found")));
    }

    @Test
    void createActiveSubstance_success() throws Exception {
        UUID id = UUID.randomUUID();
        ActiveSubstanceCreateDto createDto = sampleCreateDto("Substance C");

        ActiveSubstance entityFromDto = sampleEntity(null, "Substance C");
        ActiveSubstance createdEntity = sampleEntity(id, "Substance C");
        ActiveSubstanceDto responseDto = sampleDto(id, "Substance C");

        when(activeSubstanceMapper.fromCreateDto(any(ActiveSubstanceCreateDto.class))).thenReturn(entityFromDto);
        when(activeSubstanceService.createActiveSubstance(entityFromDto)).thenReturn(createdEntity);
        when(activeSubstanceMapper.toDto(createdEntity)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/active-substances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Substance C")));
    }

    @Test
    void createActiveSubstance_alreadyExists() throws Exception {
        ActiveSubstanceCreateDto createDto = sampleCreateDto("Duplicate");
        ActiveSubstance entityFromDto = sampleEntity(null, "Duplicate");

        when(activeSubstanceMapper.fromCreateDto(any(ActiveSubstanceCreateDto.class))).thenReturn(entityFromDto);
        when(activeSubstanceService.createActiveSubstance(entityFromDto))
                .thenThrow(new ActiveSubstanceAlreadyExistsException("Already exists"));

        mockMvc.perform(post("/api/v1/active-substances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", containsString("Already exists")));
    }

    @Test
    void createActiveSubstance_validationFails_emptyName() throws Exception {
        ActiveSubstanceCreateDto createDto = sampleCreateDto("");

        mockMvc.perform(post("/api/v1/active-substances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateActiveSubstance_success() throws Exception {
        UUID id = UUID.randomUUID();
        ActiveSubstanceUpdateDto updateDto = sampleUpdateDto("Updated");
        ActiveSubstance entityFromDto = sampleEntity(null, "Updated");
        ActiveSubstance updatedEntity = sampleEntity(id, "Updated");
        ActiveSubstanceDto responseDto = sampleDto(id, "Updated");

        when(activeSubstanceMapper.fromUpdateDto(any(ActiveSubstanceUpdateDto.class))).thenReturn(entityFromDto);
        when(activeSubstanceService.updateActiveSubstance(eq(id), eq(entityFromDto))).thenReturn(updatedEntity);
        when(activeSubstanceMapper.toDto(updatedEntity)).thenReturn(responseDto);

        mockMvc.perform(patch("/api/v1/active-substances/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Updated")));
    }

    @Test
    void updateActiveSubstance_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        ActiveSubstanceUpdateDto updateDto = sampleUpdateDto("Nope");
        ActiveSubstance entityFromDto = sampleEntity(null, "Nope");

        when(activeSubstanceMapper.fromUpdateDto(any(ActiveSubstanceUpdateDto.class))).thenReturn(entityFromDto);
        when(activeSubstanceService.updateActiveSubstance(eq(id), eq(entityFromDto)))
                .thenThrow(new ActiveSubstanceNotFoundException("Not found"));

        mockMvc.perform(patch("/api/v1/active-substances/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    @Test
    void updateActiveSubstance_alreadyExists() throws Exception {
        UUID id = UUID.randomUUID();
        ActiveSubstanceUpdateDto updateDto = sampleUpdateDto("Exists");
        ActiveSubstance entityFromDto = sampleEntity(null, "Exists");

        when(activeSubstanceMapper.fromUpdateDto(any(ActiveSubstanceUpdateDto.class))).thenReturn(entityFromDto);
        when(activeSubstanceService.updateActiveSubstance(eq(id), eq(entityFromDto)))
                .thenThrow(new ActiveSubstanceAlreadyExistsException("Already exists"));

        mockMvc.perform(patch("/api/v1/active-substances/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")));
    }

    @Test
    void updateActiveSubstance_validationFails_emptyName() throws Exception {
        UUID id = UUID.randomUUID();
        ActiveSubstanceUpdateDto updateDto = sampleUpdateDto("");

        mockMvc.perform(patch("/api/v1/active-substances/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteActiveSubstance_success() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(activeSubstanceService).deleteActiveSubstance(id);

        mockMvc.perform(delete("/api/v1/active-substances/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteActiveSubstance_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new ActiveSubstanceNotFoundException("Not found")).when(activeSubstanceService).deleteActiveSubstance(id);

        mockMvc.perform(delete("/api/v1/active-substances/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }
}