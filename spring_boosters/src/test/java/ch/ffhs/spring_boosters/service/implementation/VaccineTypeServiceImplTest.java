package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.entity.VaccineType;
import ch.ffhs.spring_boosters.repository.VaccineTypeRepository;
import ch.ffhs.spring_boosters.service.Exception.VaccineTypeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;

import java.util.*;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VaccineTypeServiceImplTest {

    @Mock
    private VaccineTypeRepository vaccineTypeRepository;

    @InjectMocks
    private VaccineTypeServiceImpl vaccineTypeService;

    private VaccineType vt1;
    private VaccineType vt2;

    @BeforeEach
    void setUp() {
        vt1 = new VaccineType();
        vt1.setId(UUID.randomUUID());
        vt1.setName("Vaccine A");

        vt2 = new VaccineType();
        vt2.setId(UUID.randomUUID());
        vt2.setName("Vaccine B");
    }

    @Test
    void getVaccineTypes_returnsAll() {
        when(vaccineTypeRepository.findAll()).thenReturn(Arrays.asList(vt1, vt2));

        List<VaccineType> result = vaccineTypeService.getVaccineTypes();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        verify(vaccineTypeRepository, times(1)).findAll();
    }

    @Test
    void getVaccineType_found() throws VaccineTypeNotFoundException {
        UUID id = vt1.getId();
        when(vaccineTypeRepository.findById(id)).thenReturn(Optional.of(vt1));

        VaccineType result = vaccineTypeService.getVaccineType(id);

        Assertions.assertEquals("Vaccine A", result.getName());
        verify(vaccineTypeRepository, times(1)).findById(id);
    }

    @Test
    void getVaccineType_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(vaccineTypeRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(VaccineTypeNotFoundException.class, () -> vaccineTypeService.getVaccineType(id));
        verify(vaccineTypeRepository, times(1)).findById(id);
    }
}

