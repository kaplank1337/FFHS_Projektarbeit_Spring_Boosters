package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.controller.dto.VaccineTypeDto;
import ch.ffhs.spring_boosters.controller.dto.VaccineTypeListDto;
import ch.ffhs.spring_boosters.controller.entity.VaccineType;
import ch.ffhs.spring_boosters.controller.exception.GlobalExceptionHandler;
import ch.ffhs.spring_boosters.controller.mapper.VaccineTypeMapper;
import ch.ffhs.spring_boosters.service.Exception.VaccineTypeNotFoundException;
import ch.ffhs.spring_boosters.service.VaccineTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VaccineTypeControllerTest {

    @Mock
    private VaccineTypeService vaccineTypeService;

    @Mock
    private VaccineTypeMapper vaccineTypeMapper;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        VaccineTypeController controller = new VaccineTypeController(vaccineTypeService, vaccineTypeMapper);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private VaccineType sampleEntity(UUID id, String name, String code) {
        VaccineType v = new VaccineType();
        v.setId(id);
        v.setName(name);
        v.setCode(code);
        return v;
    }

    private VaccineTypeDto sampleDto(UUID id, String name, String code) {
        return new VaccineTypeDto(id, name, code, List.of());
    }

    @Test
    void getVaccineTypes_returnsList() throws Exception {
        UUID id = UUID.randomUUID();
        VaccineType entity = sampleEntity(id, "COVID-19 mRNA Moderna", "COVID-MOD");
        VaccineTypeDto dto = sampleDto(id, "COVID-19 mRNA Moderna", "COVID-MOD");

        when(vaccineTypeService.getVaccineTypes()).thenReturn(List.of(entity));
        when(vaccineTypeMapper.vaccineTypeListDto(List.of(entity))).thenReturn(new VaccineTypeListDto(List.of(dto)));

        mockMvc.perform(get("/api/v1/vaccine-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vaccineTypeDtoList", hasSize(1)))
                .andExpect(jsonPath("$.vaccineTypeDtoList[0].id", is(id.toString())))
                .andExpect(jsonPath("$.vaccineTypeDtoList[0].name", is("COVID-19 mRNA Moderna")));
    }

    @Test
    void getVaccineTypeById_success() throws Exception {
        UUID id = UUID.randomUUID();
        VaccineType entity = sampleEntity(id, "Pfizer", "COVID-PFZ");
        VaccineTypeDto dto = sampleDto(id, "Pfizer", "COVID-PFZ");

        when(vaccineTypeService.getVaccineType(id)).thenReturn(entity);
        when(vaccineTypeMapper.vaccineTypeToDto(entity)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/vaccine-types/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Pfizer")))
                .andExpect(jsonPath("$.code", is("COVID-PFZ")));
    }

    @Test
    void getVaccineTypeById_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();

        when(vaccineTypeService.getVaccineType(id)).thenThrow(new VaccineTypeNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/vaccine-types/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Vaccine Type Not Found")))
                .andExpect(jsonPath("$.message", containsString("Not found")));
    }
}

