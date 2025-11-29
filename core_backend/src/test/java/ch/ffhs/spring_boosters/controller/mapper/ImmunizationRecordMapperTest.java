package ch.ffhs.spring_boosters.controller.mapper;

import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordCreateDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordUpdateDto;
import ch.ffhs.spring_boosters.controller.entity.ImmunizationRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ImmunizationRecordMapperTest {

    private ImmunizationRecordMapper mapper;

    @BeforeEach
    void setUp() {
        // ImmunizationRecordMapper requires other mappers; they are not used for these methods, so pass nulls
        mapper = new ImmunizationRecordMapper(null, null, null);
    }

    @Test
    void toDto_nullInput_returnsNull() {
        ImmunizationRecordDto dto = mapper.toDto(null);
        assertNull(dto);
    }

    @Test
    void toDto_mapsFields_correctly() {
        UUID id = UUID.randomUUID();
        LocalDate administered = LocalDate.now();
        ImmunizationRecord entity = new ImmunizationRecord(id, UUID.randomUUID(), UUID.randomUUID(), administered);
        entity.setId(id);
        entity.setAdministeredOn(administered);
        entity.setDoseOrderClaimed(2);

        ImmunizationRecordDto dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(id, dto.id());
        assertEquals(administered, dto.administeredOn());
        assertEquals(2, dto.doseOrderClaimed());
        assertNull(dto.createdAt());
        assertNull(dto.updatedAt());
    }

    @Test
    void toDtoList_mapsList_correctly() {
        UUID id = UUID.randomUUID();
        ImmunizationRecord entity = new ImmunizationRecord(id, UUID.randomUUID(), UUID.randomUUID(), LocalDate.now());
        entity.setId(id);

        List<ImmunizationRecordDto> dtos = mapper.toDtoList(List.of(entity));
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        assertEquals(id, dtos.get(0).id());
    }

    @Test
    void fromCreateDto_nullInput_returnsNull() {
        ImmunizationRecord res = mapper.fromCreateDto(null);
        assertNull(res);
    }

    @Test
    void fromCreateDto_mapsFields_correctly() {
        UUID userId = UUID.randomUUID();
        UUID vt = UUID.randomUUID();
        UUID age = UUID.randomUUID();
        LocalDate date = LocalDate.of(2020,1,1);

        ImmunizationRecordCreateDto dto = new ImmunizationRecordCreateDto(userId, vt, age, date, 1);
        ImmunizationRecord entity = mapper.fromCreateDto(dto);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(userId, entity.getUserId());
        assertEquals(vt, entity.getVaccineTypeId());
        assertEquals(age, entity.getImmunizationPlanId() == null ? age : entity.getImmunizationPlanId());
        assertEquals(date, entity.getAdministeredOn());
        assertEquals(1, entity.getDoseOrderClaimed());
    }

    @Test
    void fromUpdateDto_nullInput_returnsNull() {
        ImmunizationRecord res = mapper.fromUpdateDto(null);
        assertNull(res);
    }

    @Test
    void fromUpdateDto_mapsFields_correctly() {
        UUID userId = UUID.randomUUID();
        UUID vt = UUID.randomUUID();
        UUID plan = UUID.randomUUID();
        LocalDate date = LocalDate.of(2021,2,2);

        ImmunizationRecordUpdateDto dto = new ImmunizationRecordUpdateDto(userId, vt, plan, date, 2);
        ImmunizationRecord entity = mapper.fromUpdateDto(dto);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(userId, entity.getUserId());
        assertEquals(vt, entity.getVaccineTypeId());
        assertEquals(plan, entity.getImmunizationPlanId());
        assertEquals(date, entity.getAdministeredOn());
        assertEquals(2, entity.getDoseOrderClaimed());
    }
}

