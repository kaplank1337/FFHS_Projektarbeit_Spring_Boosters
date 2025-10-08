package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.entity.AgeCategory;
import ch.ffhs.spring_boosters.repository.AgeCategoryRepository;
import ch.ffhs.spring_boosters.service.Exception.AgeCategoryAlreadyExistsException;
import ch.ffhs.spring_boosters.service.Exception.AgeCategoryNotFoundException;
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
class AgeCategoryServiceImplTest {

    @Mock
    private AgeCategoryRepository ageCategoryRepository;

    @InjectMocks
    private AgeCategoryServiceImpl ageCategoryService;

    private AgeCategory cat1;
    private AgeCategory cat2;

    @BeforeEach
    void setUp() {
        cat1 = new AgeCategory();
        cat1.setId(UUID.randomUUID());
        cat1.setName("Child");
        cat1.setAgeMinDays(0);
        cat1.setAgeMaxDays(3650);

        cat2 = new AgeCategory();
        cat2.setId(UUID.randomUUID());
        cat2.setName("Adult");
        cat2.setAgeMinDays(3651);
        cat2.setAgeMaxDays(20000);
    }

    @Test
    void getAllAgeCategories_returnsAll() {
        List<AgeCategory> list = Arrays.asList(cat1, cat2);
        when(ageCategoryRepository.findAll()).thenReturn(list);

        List<AgeCategory> result = ageCategoryService.getAllAgeCategories();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(cat2));
        verify(ageCategoryRepository, times(1)).findAll();
    }

    @Test
    void getAgeCategoryById_found() throws AgeCategoryNotFoundException {
        UUID id = cat1.getId();
        when(ageCategoryRepository.findById(id)).thenReturn(Optional.of(cat1));

        AgeCategory result = ageCategoryService.getAgeCategoryById(id);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Child", result.getName());
        verify(ageCategoryRepository, times(1)).findById(id);
    }

    @Test
    void getAgeCategoryById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(ageCategoryRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(AgeCategoryNotFoundException.class, () -> ageCategoryService.getAgeCategoryById(id));
        verify(ageCategoryRepository, times(1)).findById(id);
    }

    @Test
    void createAgeCategory_success() throws AgeCategoryAlreadyExistsException {
        AgeCategory toCreate = new AgeCategory();
        toCreate.setName("Senior");
        toCreate.setAgeMinDays(20001);
        toCreate.setAgeMaxDays(99999);

        when(ageCategoryRepository.existsByName("Senior")).thenReturn(false);
        when(ageCategoryRepository.save(any(AgeCategory.class))).thenAnswer(invocation -> {
            AgeCategory arg = invocation.getArgument(0);
            arg.setId(UUID.randomUUID());
            return arg;
        });

        AgeCategory result = ageCategoryService.createAgeCategory(toCreate);

        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals("Senior", result.getName());
        verify(ageCategoryRepository, times(1)).existsByName("Senior");
        verify(ageCategoryRepository, times(1)).save(any(AgeCategory.class));
    }

    @Test
    void createAgeCategory_alreadyExists_throws() {
        AgeCategory toCreate = new AgeCategory();
        toCreate.setName("Child");
        when(ageCategoryRepository.existsByName("Child")).thenReturn(true);

        Assertions.assertThrows(AgeCategoryAlreadyExistsException.class, () -> ageCategoryService.createAgeCategory(toCreate));
        verify(ageCategoryRepository, times(1)).existsByName("Child");
        verify(ageCategoryRepository, never()).save(any());
    }

    @Test
    void updateAgeCategory_success() throws Exception {
        UUID id = cat1.getId();
        AgeCategory updated = new AgeCategory();
        updated.setName("Infant");
        updated.setAgeMinDays(0);
        updated.setAgeMaxDays(365);

        when(ageCategoryRepository.findById(id)).thenReturn(Optional.of(cat1));
        when(ageCategoryRepository.existsByName("Infant")).thenReturn(false);
        when(ageCategoryRepository.save(any(AgeCategory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AgeCategory result = ageCategoryService.updateAgeCategory(id, updated);

        Assertions.assertEquals("Infant", result.getName());
        Assertions.assertEquals(365, result.getAgeMaxDays());
        verify(ageCategoryRepository, times(1)).findById(id);
        verify(ageCategoryRepository, times(1)).existsByName("Infant");
        verify(ageCategoryRepository, times(1)).save(any(AgeCategory.class));
    }

    @Test
    void updateAgeCategory_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(ageCategoryRepository.findById(id)).thenReturn(Optional.empty());

        AgeCategory updated = new AgeCategory();
        updated.setName("Any");

        Assertions.assertThrows(AgeCategoryNotFoundException.class, () -> ageCategoryService.updateAgeCategory(id, updated));
        verify(ageCategoryRepository, times(1)).findById(id);
    }

    @Test
    void updateAgeCategory_nameAlreadyExists_throws() {
        UUID id = cat1.getId();
        AgeCategory updated = new AgeCategory();
        updated.setName("Adult");

        when(ageCategoryRepository.findById(id)).thenReturn(Optional.of(cat1));
        when(ageCategoryRepository.existsByName("Adult")).thenReturn(true);

        Assertions.assertThrows(AgeCategoryAlreadyExistsException.class, () -> ageCategoryService.updateAgeCategory(id, updated));
        verify(ageCategoryRepository, times(1)).findById(id);
        verify(ageCategoryRepository, times(1)).existsByName("Adult");
        verify(ageCategoryRepository, never()).save(any());
    }

    @Test
    void deleteAgeCategory_success() throws AgeCategoryNotFoundException {
        UUID id = cat2.getId();
        when(ageCategoryRepository.existsById(id)).thenReturn(true);

        ageCategoryService.deleteAgeCategory(id);

        verify(ageCategoryRepository, times(1)).existsById(id);
        verify(ageCategoryRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteAgeCategory_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(ageCategoryRepository.existsById(id)).thenReturn(false);

        Assertions.assertThrows(AgeCategoryNotFoundException.class, () -> ageCategoryService.deleteAgeCategory(id));
        verify(ageCategoryRepository, times(1)).existsById(id);
        verify(ageCategoryRepository, never()).deleteById(any());
    }
}

