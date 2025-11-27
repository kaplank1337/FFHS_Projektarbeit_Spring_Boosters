package ch.ffhs.spring_boosters.controller.mapper;

import ch.ffhs.spring_boosters.controller.dto.VaccineTypeDto;
import ch.ffhs.spring_boosters.controller.dto.VaccineTypeListDto;
import ch.ffhs.spring_boosters.controller.entity.VaccineType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VaccineTypeMapperTest {

    private VaccineTypeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new VaccineTypeMapper();
    }

    private VaccineType sampleEntity(UUID id, String name, String code) {
        VaccineType v = new VaccineType();
        v.setId(id);
        v.setName(name);
        v.setCode(code);
        v.setVaccineTypeActiveSubstances(List.of());
        return v;
    }

    private VaccineTypeDto sampleDto(UUID id, String name, String code) {
        return new VaccineTypeDto(id, name, code, List.of());
    }

    @Test
    void vaccineTypeToDto_null_returnsNull() {
        VaccineTypeDto dto = mapper.vaccineTypeToDto(null);
        assertNull(dto);
    }

    @Test
    void vaccineTypeToDto_mapsFields_correctly() {
        UUID id = UUID.randomUUID();
        VaccineType entity = sampleEntity(id, "Moderna", "COVID-MOD");
        VaccineTypeDto dto = mapper.vaccineTypeToDto(entity);

        assertNotNull(dto);
        assertEquals(id, dto.id());
        assertEquals("Moderna", dto.name());
        assertEquals("COVID-MOD", dto.code());
        assertNotNull(dto.vaccineTypeActiveSubstances());
    }

    @Test
    void vaccineTypeListDto_mapsList_correctly() {
        UUID id = UUID.randomUUID();
        VaccineType entity = sampleEntity(id, "Pfizer", "COVID-PFZ");

        VaccineTypeListDto listDto = mapper.vaccineTypeListDto(List.of(entity));
        assertNotNull(listDto);
        assertEquals(1, listDto.vaccineTypeDtoList().size());
        assertEquals(id, listDto.vaccineTypeDtoList().get(0).id());
    }
}
