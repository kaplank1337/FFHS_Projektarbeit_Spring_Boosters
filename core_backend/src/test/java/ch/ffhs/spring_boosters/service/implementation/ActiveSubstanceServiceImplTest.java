package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.entity.ActiveSubstance;
import ch.ffhs.spring_boosters.repository.ActiveSubstanceRepository;
import ch.ffhs.spring_boosters.service.Exception.ActiveSubstanceAlreadyExistsException;
import ch.ffhs.spring_boosters.service.Exception.ActiveSubstanceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActiveSubstanceServiceImplTest {

    @Mock
    private ActiveSubstanceRepository repository;

    @InjectMocks
    private ActiveSubstanceServiceImpl service;

    @Test
    void getAll_returnsRepositoryList() {
        ActiveSubstance a = new ActiveSubstance();
        a.setId(UUID.randomUUID());
        a.setName("Paracetamol");

        when(repository.findAll()).thenReturn(List.of(a));

        var result = service.getAllActiveSubstances();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Paracetamol", result.get(0).getName());
    }

    @Test
    void getById_found_returnsEntity() throws Exception {
        UUID id = UUID.randomUUID();
        ActiveSubstance a = new ActiveSubstance();
        a.setId(id);
        a.setName("Ibuprofen");

        when(repository.findById(id)).thenReturn(Optional.of(a));

        var found = service.getActiveSubstanceById(id);
        assertEquals("Ibuprofen", found.getName());
    }

    @Test
    void getById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ActiveSubstanceNotFoundException.class, () -> service.getActiveSubstanceById(id));
    }

    @Test
    void create_whenExists_throws() {
        ActiveSubstance a = new ActiveSubstance();
        a.setName("Aspirin");

        when(repository.existsByName("Aspirin")).thenReturn(true);

        assertThrows(ActiveSubstanceAlreadyExistsException.class, () -> service.createActiveSubstance(a));
        verify(repository, never()).save(any());
    }

    @Test
    void create_whenNotExists_saves() throws Exception {
        ActiveSubstance a = new ActiveSubstance();
        a.setName("NewSub");

        when(repository.existsByName("NewSub")).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> {
            ActiveSubstance arg = inv.getArgument(0);
            arg.setId(UUID.randomUUID());
            return arg;
        });

        var saved = service.createActiveSubstance(a);
        assertNotNull(saved.getId());
        assertEquals("NewSub", saved.getName());
    }

    @Test
    void update_existing_updatesFields() throws Exception {
        UUID id = UUID.randomUUID();
        ActiveSubstance existing = new ActiveSubstance();
        existing.setId(id);
        existing.setName("Old");

        ActiveSubstance update = new ActiveSubstance();
        update.setName("Updated");
        update.setSynonyms(new String[]{"syn"});

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.updateActiveSubstance(id, update);
        assertEquals("Updated", result.getName());
        assertArrayEquals(new String[]{"syn"}, result.getSynonyms());
    }

    @Test
    void update_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        ActiveSubstance update = new ActiveSubstance();
        assertThrows(ActiveSubstanceNotFoundException.class, () -> service.updateActiveSubstance(id, update));
    }

    @Test
    void delete_whenExists_deletes() throws Exception {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        service.deleteActiveSubstance(id);

        verify(repository).deleteById(id);
    }

    @Test
    void delete_notExists_throws() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(ActiveSubstanceNotFoundException.class, () -> service.deleteActiveSubstance(id));
    }
}
