package ch.ffhs.spring_boosters.controller.mapper;

import ch.ffhs.spring_boosters.controller.dto.ImmunizationPlanCreateDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationPlanDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationPlanUpdateDto;
import ch.ffhs.spring_boosters.controller.entity.ImmunizationPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ImmunizationPlanMapperTest {

    private ImmunizationPlanMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ImmunizationPlanMapper();
    }

    @Test
    void toDto_nullInput_returnsNull() {
        ImmunizationPlanDto dto = mapper.toDto(null);
        assertNull(dto);
    }

    @Test
    void toDto_mapsFields_correctly() {
        UUID id = UUID.randomUUID();
        UUID vt = UUID.randomUUID();
        UUID ac = UUID.randomUUID();
        ImmunizationPlan entity = new ImmunizationPlan("Plan A", vt, ac);
        entity.setId(id);
        entity.setName("Plan A");
        entity.setVaccineTypeId(vt);
        entity.setAgeCategoryId(ac);

        ImmunizationPlanDto dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(id, dto.id());
        assertEquals("Plan A", dto.name());
        assertEquals(vt, dto.vaccineTypeId());
        assertEquals(ac, dto.ageCategoryId());
        // createdAt/updatedAt are JPA-managed and null in unit test
        assertNull(dto.createdAt());
        assertNull(dto.updatedAt());
    }

    @Test
    void toDtoList_mapsList_correctly() {
        UUID id = UUID.randomUUID();
        UUID vt = UUID.randomUUID();
        UUID ac = UUID.randomUUID();
        ImmunizationPlan entity = new ImmunizationPlan("Plan B", vt, ac);
        entity.setId(id);

        List<ImmunizationPlanDto> dtos = mapper.toDtoList(List.of(entity));
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        assertEquals(id, dtos.get(0).id());
    }

    @Test
    void fromCreateDto_nullInput_returnsNull() {
        ImmunizationPlan res = mapper.fromCreateDto(null);
        assertNull(res);
    }

    @Test
    void fromCreateDto_mapsFields_correctly() {
        UUID vt = UUID.randomUUID();
        UUID ac = UUID.randomUUID();
        ImmunizationPlanCreateDto dto = new ImmunizationPlanCreateDto("Name", vt, ac);

        ImmunizationPlan entity = mapper.fromCreateDto(dto);
        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals("Name", entity.getName());
        assertEquals(vt, entity.getVaccineTypeId());
        assertEquals(ac, entity.getAgeCategoryId());
    }

    @Test
    void fromUpdateDto_nullInput_returnsNull() {
        ImmunizationPlan res = mapper.fromUpdateDto(null);
        assertNull(res);
    }

    @Test
    void fromUpdateDto_mapsFields_correctly() {
        UUID vt = UUID.randomUUID();
        UUID ac = UUID.randomUUID();
        ImmunizationPlanUpdateDto dto = new ImmunizationPlanUpdateDto("Updated", vt, ac);

        ImmunizationPlan entity = mapper.fromUpdateDto(dto);
        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals("Updated", entity.getName());
        assertEquals(vt, entity.getVaccineTypeId());
        assertEquals(ac, entity.getAgeCategoryId());
    }
}

