package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.entity.AgeCategory;
import ch.ffhs.spring_boosters.repository.AgeCategoryRepository;
import ch.ffhs.spring_boosters.service.Exception.AgeCategoryAlreadyExistsException;
import ch.ffhs.spring_boosters.service.Exception.AgeCategoryNotFoundException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgeCategoryServiceImplTest {

    @Mock
    private AgeCategoryRepository repository;

    @InjectMocks
    private AgeCategoryServiceImpl service;

    @Test
    void getAll_returnsAll() {
        AgeCategory c = new AgeCategory();
        c.setId(UUID.randomUUID());
        c.setName("Säugling");

        when(repository.findAll()).thenReturn(List.of(c));

        var res = service.getAllAgeCategories();
        assertEquals(1, res.size());
        assertEquals("Säugling", res.get(0).getName());
    }

    @Test
    void getById_found() throws Exception {
        UUID id = UUID.randomUUID();
        AgeCategory c = new AgeCategory();
        c.setId(id);
        c.setName("Kind");

        when(repository.findById(id)).thenReturn(Optional.of(c));

        var found = service.getAgeCategoryById(id);
        assertEquals("Kind", found.getName());
    }

    @Test
    void getById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AgeCategoryNotFoundException.class, () -> service.getAgeCategoryById(id));
    }

    @Test
    void create_exists_throws() {
        AgeCategory c = new AgeCategory();
        c.setName("Adult");
        when(repository.existsByName("Adult")).thenReturn(true);

        assertThrows(AgeCategoryAlreadyExistsException.class, () -> service.createAgeCategory(c));
    }

    @Test
    void create_notExists_saves() throws Exception {
        AgeCategory c = new AgeCategory();
        c.setName("Youth");
        when(repository.existsByName("Youth")).thenReturn(false);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        var saved = service.createAgeCategory(c);
        assertEquals("Youth", saved.getName());
    }

    @Test
    void update_changeNameToExisting_throws() {
        UUID id = UUID.randomUUID();
        AgeCategory existing = new AgeCategory();
        existing.setId(id);
        existing.setName("Old");

        AgeCategory update = new AgeCategory();
        update.setName("New");

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.existsByName("New")).thenReturn(true);

        assertThrows(AgeCategoryAlreadyExistsException.class, () -> service.updateAgeCategory(id, update));
    }

    @Test
    void update_success() throws Exception {
        UUID id = UUID.randomUUID();
        AgeCategory existing = new AgeCategory();
        existing.setId(id);
        existing.setName("Old");
        existing.setAgeMinDays(0);
        existing.setAgeMaxDays(100);

        AgeCategory update = new AgeCategory();
        update.setName("Updated");
        update.setAgeMinDays(1);
        update.setAgeMaxDays(200);

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.existsByName("Updated")).thenReturn(false);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        var res = service.updateAgeCategory(id, update);
        assertEquals("Updated", res.getName());
        assertEquals(1, res.getAgeMinDays());
        assertEquals(200, res.getAgeMaxDays());
    }

    @Test
    void delete_exists_deletes() throws Exception {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        service.deleteAgeCategory(id);
        verify(repository).deleteById(id);
    }

    @Test
    void delete_notExists_throws() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(AgeCategoryNotFoundException.class, () -> service.deleteAgeCategory(id));
    }
}

