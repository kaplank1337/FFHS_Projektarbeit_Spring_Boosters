package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.entity.VaccineType;
import ch.ffhs.spring_boosters.repository.VaccineTypeRepository;
import ch.ffhs.spring_boosters.service.Exception.VaccineTypeNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VaccineTypeServiceImplTest {

    @Mock
    private VaccineTypeRepository repository;

    @InjectMocks
    private VaccineTypeServiceImpl service;

    @Test
    void getAll_returnsAll() {
        VaccineType v = new VaccineType();
        v.setId(UUID.randomUUID());
        v.setName("COVID");

        when(repository.findAll()).thenReturn(List.of(v));

        var res = service.getVaccineTypes();
        assertEquals(1, res.size());
    }

    @Test
    void getById_found() throws Exception {
        UUID id = UUID.randomUUID();
        VaccineType v = new VaccineType();
        v.setId(id);
        v.setName("X");

        when(repository.findById(id)).thenReturn(Optional.of(v));
        var found = service.getVaccineType(id);
        assertEquals("X", found.getName());
    }

    @Test
    void getById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(VaccineTypeNotFoundException.class, () -> service.getVaccineType(id));
    }
}

