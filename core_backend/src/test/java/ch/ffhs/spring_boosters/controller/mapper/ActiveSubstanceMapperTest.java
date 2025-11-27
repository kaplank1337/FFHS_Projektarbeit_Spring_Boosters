package ch.ffhs.spring_boosters.controller.mapper;

import ch.ffhs.spring_boosters.controller.dto.ActiveSubstanceCreateDto;
import ch.ffhs.spring_boosters.controller.dto.ActiveSubstanceDto;
import ch.ffhs.spring_boosters.controller.dto.ActiveSubstanceUpdateDto;
import ch.ffhs.spring_boosters.controller.entity.ActiveSubstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ActiveSubstanceMapperTest {

    private ActiveSubstanceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ActiveSubstanceMapper();
    }

    @Test
    void toDto_nullInput_returnsNull() {
        ActiveSubstanceDto dto = mapper.toDto(null);
        assertNull(dto, "Expected null when input is null");
    }

    @Test
    void toDto_mapsFields_correctly() {
        UUID id = UUID.randomUUID();
        ActiveSubstance entity = new ActiveSubstance("Paracetamol", new String[]{"Acetaminophen"});
        entity.setId(id);
        entity.setName("Paracetamol");
        entity.setSynonyms(new String[]{"Acetaminophen"});

        ActiveSubstanceDto dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(id, dto.id());
        assertEquals("Paracetamol", dto.name());
        assertArrayEquals(new String[]{"Acetaminophen"}, dto.synonyms());
        assertNull(dto.createdAt());
        assertNull(dto.updatedAt());
    }

    @Test
    void toDtoList_mapsList_correctly() {
        UUID id = UUID.randomUUID();
        ActiveSubstance entity = new ActiveSubstance("Ibuprofen", new String[]{"Ibu"});
        entity.setId(id);
        entity.setName("Ibuprofen");
        entity.setSynonyms(new String[]{"Ibu"});

        List<ActiveSubstanceDto> dtos = mapper.toDtoList(List.of(entity));

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        ActiveSubstanceDto dto = dtos.get(0);
        assertEquals(id, dto.id());
        assertEquals("Ibuprofen", dto.name());
        assertArrayEquals(new String[]{"Ibu"}, dto.synonyms());
    }

    @Test
    void fromCreateDto_nullInput_returnsNull() {
        ActiveSubstance res = mapper.fromCreateDto(null);
        assertNull(res);
    }

    @Test
    void fromCreateDto_mapsFields_correctly() {
        ActiveSubstanceCreateDto createDto = new ActiveSubstanceCreateDto("Aspirin", new String[]{"Acetylsalicylic Acid"});
        ActiveSubstance entity = mapper.fromCreateDto(createDto);

        assertNotNull(entity);
        assertNull(entity.getId(), "New entity should not have id set");
        assertEquals("Aspirin", entity.getName());
        assertArrayEquals(new String[]{"Acetylsalicylic Acid"}, entity.getSynonyms());
    }

    @Test
    void fromUpdateDto_nullInput_returnsNull() {
        ActiveSubstance res = mapper.fromUpdateDto(null);
        assertNull(res);
    }

    @Test
    void fromUpdateDto_mapsFields_correctly() {
        ActiveSubstanceUpdateDto updateDto = new ActiveSubstanceUpdateDto("Vitamin C", new String[]{"Ascorbic Acid"});
        ActiveSubstance entity = mapper.fromUpdateDto(updateDto);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals("Vitamin C", entity.getName());
        assertArrayEquals(new String[]{"Ascorbic Acid"}, entity.getSynonyms());
    }
}

