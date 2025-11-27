package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.config.JwtTokenReader;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordCreateDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordUpdateDto;
import ch.ffhs.spring_boosters.controller.entity.ImmunizationRecord;
import ch.ffhs.spring_boosters.controller.mapper.ImmunizationRecordMapper;
import ch.ffhs.spring_boosters.service.ImmunizationRecordService;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationRecordNotFoundException;
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

import java.time.LocalDate;
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
class ImmunizationRecordControllerTest {

    @Mock
    private ImmunizationRecordService immunizationRecordService;

    @Mock
    private ImmunizationRecordMapper immunizationRecordMapper;

    @Mock
    private JwtTokenReader jwtTokenReader;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ImmunizationRecordController controller = new ImmunizationRecordController(immunizationRecordService, immunizationRecordMapper, jwtTokenReader);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // helpers
    private ImmunizationRecord sampleEntity(UUID id, UUID userId, UUID vaccineTypeId, UUID planId, LocalDate administeredOn, Integer doseOrder) {
        ImmunizationRecord e = new ImmunizationRecord();
        e.setId(id);
        e.setUserId(userId);
        e.setVaccineTypeId(vaccineTypeId);
        e.setImmunizationPlanId(planId);
        e.setAdministeredOn(administeredOn);
        e.setDoseOrderClaimed(doseOrder);
        return e;
    }

    private ImmunizationRecordDto sampleDto(UUID id, LocalDate administeredOn, Integer doseOrder) {
        OffsetDateTime now = OffsetDateTime.now();
        return new ImmunizationRecordDto(id, administeredOn, doseOrder, now, now);
    }

    private ImmunizationRecordCreateDto sampleCreateDto(UUID userId, UUID vaccineTypeId, UUID ageCategoryId, LocalDate administeredOn, Integer doseOrder) {
        return new ImmunizationRecordCreateDto(userId, vaccineTypeId, ageCategoryId, administeredOn, doseOrder);
    }

    private ImmunizationRecordUpdateDto sampleUpdateDto(UUID userId, UUID vaccineTypeId, UUID planId, LocalDate administeredOn, Integer doseOrder) {
        return new ImmunizationRecordUpdateDto(userId, vaccineTypeId, planId, administeredOn, doseOrder);
    }

    @Test
    void getAllImmunizationRecords_returnsList() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID vt = UUID.randomUUID();
        UUID plan = UUID.randomUUID();
        ImmunizationRecord entity = sampleEntity(id, userId, vt, plan, LocalDate.now(), 1);
        ImmunizationRecordDto dto = sampleDto(id, LocalDate.now(), 1);

        when(immunizationRecordService.getAllImmunizationRecords()).thenReturn(List.of(entity));
        when(immunizationRecordMapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/immunization-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(id.toString())));
    }

    @Test
    void getById_success() throws Exception {
        UUID id = UUID.randomUUID();
        ImmunizationRecord entity = sampleEntity(id, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), 1);
        ImmunizationRecordDto dto = sampleDto(id, LocalDate.now(), 1);

        when(immunizationRecordService.getImmunizationRecordById(id)).thenReturn(entity);
        when(immunizationRecordMapper.toDto(entity)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/immunization-records/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())));
    }

    @Test
    void getById_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(immunizationRecordService.getImmunizationRecordById(id)).thenThrow(new ImmunizationRecordNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/immunization-records/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    @Test
    void createImmunizationRecord_success() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID vt = UUID.randomUUID();
        UUID ageCat = UUID.randomUUID();
        LocalDate date = LocalDate.now();

        ImmunizationRecordCreateDto createDto = sampleCreateDto(userId, vt, ageCat, date, 1);
        ImmunizationRecord entityFromDto = sampleEntity(null, userId, vt, UUID.randomUUID(), date, 1);
        ImmunizationRecord createdEntity = sampleEntity(id, userId, vt, UUID.randomUUID(), date, 1);
        ImmunizationRecordDto responseDto = sampleDto(id, date, 1);

        when(immunizationRecordMapper.fromCreateDto(any(ImmunizationRecordCreateDto.class))).thenReturn(entityFromDto);
        when(immunizationRecordService.createImmunizationRecord(entityFromDto)).thenReturn(createdEntity);
        when(immunizationRecordMapper.toDto(createdEntity)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/immunization-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(id.toString())));
    }

    @Test
    void createImmunizationRecord_validationFails_missingFields() throws Exception {
        // missing userId -> validation fails
        ImmunizationRecordCreateDto createDto = new ImmunizationRecordCreateDto(null, null, null, null, null);

        mockMvc.perform(post("/api/v1/immunization-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateImmunizationRecord_success() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID vt = UUID.randomUUID();
        UUID plan = UUID.randomUUID();
        LocalDate date = LocalDate.now();

        ImmunizationRecordUpdateDto updateDto = sampleUpdateDto(userId, vt, plan, date, 2);
        ImmunizationRecord entityFromDto = sampleEntity(null, userId, vt, plan, date, 2);
        ImmunizationRecord updatedEntity = sampleEntity(id, userId, vt, plan, date, 2);
        ImmunizationRecordDto responseDto = sampleDto(id, date, 2);

        when(immunizationRecordMapper.fromUpdateDto(any(ImmunizationRecordUpdateDto.class))).thenReturn(entityFromDto);
        when(immunizationRecordService.updateImmunizationRecord(eq(id), eq(entityFromDto))).thenReturn(updatedEntity);
        when(immunizationRecordMapper.toDto(updatedEntity)).thenReturn(responseDto);

        mockMvc.perform(patch("/api/v1/immunization-records/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())));
    }

    @Test
    void updateImmunizationRecord_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        ImmunizationRecordUpdateDto updateDto = sampleUpdateDto(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), 1);
        ImmunizationRecord entityFromDto = sampleEntity(null, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), 1);

        when(immunizationRecordMapper.fromUpdateDto(any(ImmunizationRecordUpdateDto.class))).thenReturn(entityFromDto);
        when(immunizationRecordService.updateImmunizationRecord(eq(id), eq(entityFromDto)))
                .thenThrow(new ImmunizationRecordNotFoundException("Not found"));

        mockMvc.perform(patch("/api/v1/immunization-records/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    @Test
    void updateImmunizationRecord_validationFails() throws Exception {
        UUID id = UUID.randomUUID();
        ImmunizationRecordUpdateDto updateDto = new ImmunizationRecordUpdateDto(null, null, null, null, null);

        mockMvc.perform(patch("/api/v1/immunization-records/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteImmunizationRecord_success() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String token = "Bearer fakeToken";

        // jwtTokenReader returns userId string when asked with the raw token
        when(jwtTokenReader.getUserId("fakeToken")).thenReturn(userId.toString());
        doNothing().when(immunizationRecordService).deleteImmunizationRecord(userId, id);

        mockMvc.perform(delete("/api/v1/immunization-records/" + id).header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteImmunizationRecord_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String token = "Bearer fakeToken";

        when(jwtTokenReader.getUserId("fakeToken")).thenReturn(userId.toString());
        doThrow(new ImmunizationRecordNotFoundException("Not found")).when(immunizationRecordService).deleteImmunizationRecord(userId, id);

        mockMvc.perform(delete("/api/v1/immunization-records/" + id).header("Authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void deleteImmunizationRecord_invalidToken_returnsBadRequest() throws Exception {
        UUID id = UUID.randomUUID();
        String token = "Bearer badToken";

        when(jwtTokenReader.getUserId("badToken")).thenReturn("not-a-uuid");

        mockMvc.perform(delete("/api/v1/immunization-records/" + id).header("Authorization", token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByUser_returnsList() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        ImmunizationRecord entity = sampleEntity(id, userId, UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), 1);
        ImmunizationRecordDto dto = sampleDto(id, LocalDate.now(), 1);

        when(immunizationRecordService.getImmunizationRecordsByUser(userId)).thenReturn(List.of(entity));
        when(immunizationRecordMapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/immunization-records/by-user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(id.toString())));
    }

    @Test
    void getByVaccineType_returnsList() throws Exception {
        UUID vt = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        ImmunizationRecord entity = sampleEntity(id, UUID.randomUUID(), vt, UUID.randomUUID(), LocalDate.now(), 1);
        ImmunizationRecordDto dto = sampleDto(id, LocalDate.now(), 1);

        when(immunizationRecordService.getImmunizationRecordsByVaccineType(vt)).thenReturn(List.of(entity));
        when(immunizationRecordMapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/immunization-records/by-vaccine-type/" + vt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(id.toString())));
    }

    @Test
    void getByUserAndVaccineType_returnsList() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID vt = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        ImmunizationRecord entity = sampleEntity(id, userId, vt, UUID.randomUUID(), LocalDate.now(), 1);
        ImmunizationRecordDto dto = sampleDto(id, LocalDate.now(), 1);

        when(immunizationRecordService.getImmunizationRecordsByUserAndVaccineType(userId, vt)).thenReturn(List.of(entity));
        when(immunizationRecordMapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/immunization-records/by-user/" + userId + "/vaccine-type/" + vt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(id.toString())));
    }

    @Test
    void getMyImmunizationRecords_success() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        String token = "Bearer myToken";

        when(jwtTokenReader.getUserId("myToken")).thenReturn(userId.toString());
        ImmunizationRecord entity = sampleEntity(id, userId, UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), 1);
        ImmunizationRecordDto dto = sampleDto(id, LocalDate.now(), 1);

        when(immunizationRecordService.getImmunizationRecordsByUser(userId)).thenReturn(List.of(entity));
        when(immunizationRecordMapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/immunization-records/myVaccinations").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getMyImmunizationRecords_invalidToken_badRequest() throws Exception {
        String token = "Bearer bad";
        when(jwtTokenReader.getUserId("bad")).thenReturn("not-a-uuid");

        mockMvc.perform(get("/api/v1/immunization-records/myVaccinations").header("Authorization", token))
                .andExpect(status().isBadRequest());
    }
}

