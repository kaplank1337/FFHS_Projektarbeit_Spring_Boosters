package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.entity.ActiveSubstance;
import ch.ffhs.spring_boosters.repository.ActiveSubstanceRepository;
import ch.ffhs.spring_boosters.service.Exception.ActiveSubstanceAlreadyExistsException;
import ch.ffhs.spring_boosters.service.Exception.ActiveSubstanceNotFoundException;
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
class ActiveSubstanceServiceImplTest {

    @Mock
    private ActiveSubstanceRepository activeSubstanceRepository;

    @InjectMocks
    private ActiveSubstanceServiceImpl activeSubstanceService;

    private ActiveSubstance substance1;
    private ActiveSubstance substance2;

    @BeforeEach
    void setUp() {
        substance1 = new ActiveSubstance("Substance A", new String[]{"A1", "A2"});
        substance1.setId(UUID.randomUUID());

        substance2 = new ActiveSubstance("Substance B", new String[]{"B1"});
        substance2.setId(UUID.randomUUID());
    }

    @Test
    void getAllActiveSubstances_returnsAll() {
        List<ActiveSubstance> list = Arrays.asList(substance1, substance2);
        when(activeSubstanceRepository.findAll()).thenReturn(list);

        List<ActiveSubstance> result = activeSubstanceService.getAllActiveSubstances();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(substance1));
        verify(activeSubstanceRepository, times(1)).findAll();
    }

    @Test
    void getActiveSubstanceById_found() throws ActiveSubstanceNotFoundException {
        UUID id = substance1.getId();
        when(activeSubstanceRepository.findById(id)).thenReturn(Optional.of(substance1));

        ActiveSubstance result = activeSubstanceService.getActiveSubstanceById(id);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(substance1.getName(), result.getName());
        verify(activeSubstanceRepository, times(1)).findById(id);
    }

    @Test
    void getActiveSubstanceById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(activeSubstanceRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(ActiveSubstanceNotFoundException.class, () -> activeSubstanceService.getActiveSubstanceById(id));
        verify(activeSubstanceRepository, times(1)).findById(id);
    }

    @Test
    void createActiveSubstance_success() throws ActiveSubstanceAlreadyExistsException {
        ActiveSubstance toCreate = new ActiveSubstance("NewSub", new String[]{"N"});
        when(activeSubstanceRepository.existsByName(toCreate.getName())).thenReturn(false);
        when(activeSubstanceRepository.save(any(ActiveSubstance.class))).thenAnswer(invocation -> {
            ActiveSubstance arg = invocation.getArgument(0);
            arg.setId(UUID.randomUUID());
            return arg;
        });

        ActiveSubstance result = activeSubstanceService.createActiveSubstance(toCreate);

        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals("NewSub", result.getName());
        verify(activeSubstanceRepository, times(1)).existsByName("NewSub");
        verify(activeSubstanceRepository, times(1)).save(any(ActiveSubstance.class));
    }

    @Test
    void createActiveSubstance_alreadyExists_throws() {
        ActiveSubstance toCreate = new ActiveSubstance("Substance A", new String[]{});
        when(activeSubstanceRepository.existsByName(toCreate.getName())).thenReturn(true);

        Assertions.assertThrows(ActiveSubstanceAlreadyExistsException.class, () -> activeSubstanceService.createActiveSubstance(toCreate));
        verify(activeSubstanceRepository, times(1)).existsByName("Substance A");
        verify(activeSubstanceRepository, never()).save(any());
    }

    @Test
    void updateActiveSubstance_success() throws Exception {
        UUID id = substance1.getId();
        ActiveSubstance updated = new ActiveSubstance("UpdatedName", new String[]{"X"});

        when(activeSubstanceRepository.findById(id)).thenReturn(Optional.of(substance1));
        when(activeSubstanceRepository.existsByName("UpdatedName")).thenReturn(false);
        when(activeSubstanceRepository.save(any(ActiveSubstance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ActiveSubstance result = activeSubstanceService.updateActiveSubstance(id, updated);

        Assertions.assertEquals("UpdatedName", result.getName());
        Assertions.assertArrayEquals(new String[]{"X"}, result.getSynonyms());
        verify(activeSubstanceRepository, times(1)).findById(id);
        verify(activeSubstanceRepository, times(1)).existsByName("UpdatedName");
        verify(activeSubstanceRepository, times(1)).save(any(ActiveSubstance.class));
    }

    @Test
    void updateActiveSubstance_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(activeSubstanceRepository.findById(id)).thenReturn(Optional.empty());

        ActiveSubstance updated = new ActiveSubstance("Any", new String[]{});
        Assertions.assertThrows(ActiveSubstanceNotFoundException.class, () -> activeSubstanceService.updateActiveSubstance(id, updated));
        verify(activeSubstanceRepository, times(1)).findById(id);
    }

    @Test
    void updateActiveSubstance_nameAlreadyExists_throws() {
        UUID id = substance1.getId();
        ActiveSubstance updated = new ActiveSubstance("ExistingName", new String[]{});

        when(activeSubstanceRepository.findById(id)).thenReturn(Optional.of(substance1));
        when(activeSubstanceRepository.existsByName("ExistingName")).thenReturn(true);

        Assertions.assertThrows(ActiveSubstanceAlreadyExistsException.class, () -> activeSubstanceService.updateActiveSubstance(id, updated));
        verify(activeSubstanceRepository, times(1)).findById(id);
        verify(activeSubstanceRepository, times(1)).existsByName("ExistingName");
        verify(activeSubstanceRepository, never()).save(any());
    }

    @Test
    void deleteActiveSubstance_success() throws ActiveSubstanceNotFoundException {
        UUID id = substance2.getId();
        when(activeSubstanceRepository.existsById(id)).thenReturn(true);

        activeSubstanceService.deleteActiveSubstance(id);

        verify(activeSubstanceRepository, times(1)).existsById(id);
        verify(activeSubstanceRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteActiveSubstance_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(activeSubstanceRepository.existsById(id)).thenReturn(false);

        Assertions.assertThrows(ActiveSubstanceNotFoundException.class, () -> activeSubstanceService.deleteActiveSubstance(id));
        verify(activeSubstanceRepository, times(1)).existsById(id);
        verify(activeSubstanceRepository, never()).deleteById(any());
    }
}