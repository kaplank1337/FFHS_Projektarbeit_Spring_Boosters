package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.entity.ImmunizationPlan;
import ch.ffhs.spring_boosters.repository.ImmunizationPlanRepository;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationPlanAlreadyExistsException;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationPlanNotFoundException;
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
class ImmunizationPlanServiceImplTest {

    @Mock
    private ImmunizationPlanRepository immunizationPlanRepository;

    @InjectMocks
    private ImmunizationPlanServiceImpl immunizationPlanService;

    private ImmunizationPlan plan1;
    private ImmunizationPlan plan2;

    @BeforeEach
    void setUp() {
        plan1 = new ImmunizationPlan();
        plan1.setId(UUID.randomUUID());
        plan1.setName("Plan A");
        plan1.setVaccineTypeId(UUID.randomUUID());
        plan1.setAgeCategoryId(UUID.randomUUID());

        plan2 = new ImmunizationPlan();
        plan2.setId(UUID.randomUUID());
        plan2.setName("Plan B");
        plan2.setVaccineTypeId(UUID.randomUUID());
        plan2.setAgeCategoryId(UUID.randomUUID());
    }

    @Test
    void getAllImmunizationPlans_returnsAll() {
        List<ImmunizationPlan> list = Arrays.asList(plan1, plan2);
        when(immunizationPlanRepository.findAll()).thenReturn(list);

        List<ImmunizationPlan> result = immunizationPlanService.getAllImmunizationPlans();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(plan1));
        verify(immunizationPlanRepository, times(1)).findAll();
    }

    @Test
    void getImmunizationPlanById_found() throws ImmunizationPlanNotFoundException {
        UUID id = plan1.getId();
        when(immunizationPlanRepository.findById(id)).thenReturn(Optional.of(plan1));

        ImmunizationPlan result = immunizationPlanService.getImmunizationPlanById(id);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Plan A", result.getName());
        verify(immunizationPlanRepository, times(1)).findById(id);
    }

    @Test
    void getImmunizationPlanById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(immunizationPlanRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(ImmunizationPlanNotFoundException.class, () -> immunizationPlanService.getImmunizationPlanById(id));
        verify(immunizationPlanRepository, times(1)).findById(id);
    }

    @Test
    void createImmunizationPlan_success() throws ImmunizationPlanAlreadyExistsException {
        ImmunizationPlan toCreate = new ImmunizationPlan();
        toCreate.setName("New Plan");

        when(immunizationPlanRepository.existsByName("New Plan")).thenReturn(false);
        when(immunizationPlanRepository.save(any(ImmunizationPlan.class))).thenAnswer(invocation -> {
            ImmunizationPlan arg = invocation.getArgument(0);
            arg.setId(UUID.randomUUID());
            return arg;
        });

        ImmunizationPlan result = immunizationPlanService.createImmunizationPlan(toCreate);

        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals("New Plan", result.getName());
        verify(immunizationPlanRepository, times(1)).existsByName("New Plan");
        verify(immunizationPlanRepository, times(1)).save(any(ImmunizationPlan.class));
    }

    @Test
    void createImmunizationPlan_alreadyExists_throws() {
        ImmunizationPlan toCreate = new ImmunizationPlan();
        toCreate.setName("Plan A");
        when(immunizationPlanRepository.existsByName("Plan A")).thenReturn(true);

        Assertions.assertThrows(ImmunizationPlanAlreadyExistsException.class, () -> immunizationPlanService.createImmunizationPlan(toCreate));
        verify(immunizationPlanRepository, times(1)).existsByName("Plan A");
        verify(immunizationPlanRepository, never()).save(any());
    }

    @Test
    void updateImmunizationPlan_success() throws Exception {
        UUID id = plan1.getId();
        ImmunizationPlan updated = new ImmunizationPlan();
        updated.setName("Updated Plan");
        updated.setVaccineTypeId(UUID.randomUUID());
        updated.setAgeCategoryId(UUID.randomUUID());

        when(immunizationPlanRepository.findById(id)).thenReturn(Optional.of(plan1));
        when(immunizationPlanRepository.existsByName("Updated Plan")).thenReturn(false);
        when(immunizationPlanRepository.save(any(ImmunizationPlan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ImmunizationPlan result = immunizationPlanService.updateImmunizationPlan(id, updated);

        Assertions.assertEquals("Updated Plan", result.getName());
        verify(immunizationPlanRepository, times(1)).findById(id);
        verify(immunizationPlanRepository, times(1)).existsByName("Updated Plan");
        verify(immunizationPlanRepository, times(1)).save(any(ImmunizationPlan.class));
    }

    @Test
    void updateImmunizationPlan_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(immunizationPlanRepository.findById(id)).thenReturn(Optional.empty());

        ImmunizationPlan updated = new ImmunizationPlan();
        updated.setName("Any");

        Assertions.assertThrows(ImmunizationPlanNotFoundException.class, () -> immunizationPlanService.updateImmunizationPlan(id, updated));
        verify(immunizationPlanRepository, times(1)).findById(id);
    }

    @Test
    void updateImmunizationPlan_nameAlreadyExists_throws() {
        UUID id = plan1.getId();
        ImmunizationPlan updated = new ImmunizationPlan();
        updated.setName("Plan B");

        when(immunizationPlanRepository.findById(id)).thenReturn(Optional.of(plan1));
        when(immunizationPlanRepository.existsByName("Plan B")).thenReturn(true);

        Assertions.assertThrows(ImmunizationPlanAlreadyExistsException.class, () -> immunizationPlanService.updateImmunizationPlan(id, updated));
        verify(immunizationPlanRepository, times(1)).findById(id);
        verify(immunizationPlanRepository, times(1)).existsByName("Plan B");
        verify(immunizationPlanRepository, never()).save(any());
    }

    @Test
    void deleteImmunizationPlan_success() throws ImmunizationPlanNotFoundException {
        UUID id = plan2.getId();
        when(immunizationPlanRepository.existsById(id)).thenReturn(true);

        immunizationPlanService.deleteImmunizationPlan(id);

        verify(immunizationPlanRepository, times(1)).existsById(id);
        verify(immunizationPlanRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteImmunizationPlan_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(immunizationPlanRepository.existsById(id)).thenReturn(false);

        Assertions.assertThrows(ImmunizationPlanNotFoundException.class, () -> immunizationPlanService.deleteImmunizationPlan(id));
        verify(immunizationPlanRepository, times(1)).existsById(id);
        verify(immunizationPlanRepository, never()).deleteById(any());
    }
}

