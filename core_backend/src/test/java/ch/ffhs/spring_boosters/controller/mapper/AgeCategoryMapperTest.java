package ch.ffhs.spring_boosters.controller.mapper;

import ch.ffhs.spring_boosters.controller.dto.AgeCategoryCreateDto;
import ch.ffhs.spring_boosters.controller.dto.AgeCategoryDto;
import ch.ffhs.spring_boosters.controller.dto.AgeCategoryUpdateDto;
import ch.ffhs.spring_boosters.controller.entity.AgeCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AgeCategoryMapperTest {

    private AgeCategoryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AgeCategoryMapper();
    }

    @Test
    void toDto_nullInput_returnsNull() {
        AgeCategoryDto dto = mapper.toDto(null);
        assertNull(dto, "Expected null when input is null");
    }

    @Test
    void toDto_mapsFields_correctly() {
        UUID id = UUID.randomUUID();
        AgeCategory entity = new AgeCategory("Säugling", 0, 365);
        entity.setId(id);
        entity.setName("Säugling");
        entity.setAgeMinDays(0);
        entity.setAgeMaxDays(365);

        AgeCategoryDto dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(id, dto.id());
        assertEquals("Säugling", dto.name());
        assertEquals(0, dto.ageMinDays());
        assertEquals(365, dto.ageMaxDays());
        assertNull(dto.createdAt());
        assertNull(dto.updatedAt());
    }

    @Test
    void toDtoList_mapsList_correctly() {
        UUID id = UUID.randomUUID();
        AgeCategory entity = new AgeCategory("Kind", 366, 3650);
        entity.setId(id);
        entity.setName("Kind");
        entity.setAgeMinDays(366);
        entity.setAgeMaxDays(3650);

        List<AgeCategoryDto> dtos = mapper.toDtoList(List.of(entity));

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        AgeCategoryDto dto = dtos.get(0);
        assertEquals(id, dto.id());
        assertEquals("Kind", dto.name());
        assertEquals(366, dto.ageMinDays());
        assertEquals(3650, dto.ageMaxDays());
    }

    @Test
    void fromCreateDto_nullInput_returnsNull() {
        AgeCategory res = mapper.fromCreateDto(null);
        assertNull(res);
    }

    @Test
    void fromCreateDto_mapsFields_correctly() {
        AgeCategoryCreateDto createDto = new AgeCategoryCreateDto("Erwachsene", 3651, null);
        AgeCategory entity = mapper.fromCreateDto(createDto);

        assertNotNull(entity);
        assertNull(entity.getId(), "New entity should not have id set");
        assertEquals("Erwachsene", entity.getName());
        assertEquals(3651, entity.getAgeMinDays());
        assertNull(entity.getAgeMaxDays());
    }

    @Test
    void fromUpdateDto_nullInput_returnsNull() {
        AgeCategory res = mapper.fromUpdateDto(null);
        assertNull(res);
    }

    @Test
    void fromUpdateDto_mapsFields_correctly() {
        AgeCategoryUpdateDto updateDto = new AgeCategoryUpdateDto("Senioren", 3651, 20000);
        AgeCategory entity = mapper.fromUpdateDto(updateDto);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals("Senioren", entity.getName());
        assertEquals(3651, entity.getAgeMinDays());
        assertEquals(20000, entity.getAgeMaxDays());
    }
}
