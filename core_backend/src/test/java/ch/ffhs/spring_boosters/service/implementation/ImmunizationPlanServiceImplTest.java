package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.entity.ImmunizationPlan;
import ch.ffhs.spring_boosters.repository.ImmunizationPlanRepository;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationPlanAlreadyExistsException;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationPlanNotFoundException;
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
class ImmunizationPlanServiceImplTest {

    @Mock
    private ImmunizationPlanRepository repository;

    @InjectMocks
    private ImmunizationPlanServiceImpl service;

    @Test
    void getAll_returnsAll() {
        ImmunizationPlan p = new ImmunizationPlan();
        p.setId(UUID.randomUUID());
        p.setName("Plan1");

        when(repository.findAll()).thenReturn(List.of(p));

        var res = service.getAllImmunizationPlans();
        assertEquals(1, res.size());
    }

    @Test
    void getById_found() throws Exception {
        UUID id = UUID.randomUUID();
        ImmunizationPlan p = new ImmunizationPlan();
        p.setId(id);
        p.setName("PlanX");

        when(repository.findById(id)).thenReturn(Optional.of(p));
        var found = service.getImmunizationPlanById(id);
        assertEquals("PlanX", found.getName());
    }

    @Test
    void getById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ImmunizationPlanNotFoundException.class, () -> service.getImmunizationPlanById(id));
    }

    @Test
    void create_whenExists_throws() {
        ImmunizationPlan p = new ImmunizationPlan();
        p.setName("P1");
        when(repository.existsByName("P1")).thenReturn(true);

        assertThrows(ImmunizationPlanAlreadyExistsException.class, () -> service.createImmunizationPlan(p));
    }

    @Test
    void create_whenNotExists_saves() throws Exception {
        ImmunizationPlan p = new ImmunizationPlan();
        p.setName("NewP");
        when(repository.existsByName("NewP")).thenReturn(false);
        when(repository.save(any())).thenAnswer(i -> {
            ImmunizationPlan arg = i.getArgument(0);
            arg.setId(UUID.randomUUID());
            return arg;
        });

        var saved = service.createImmunizationPlan(p);
        assertNotNull(saved.getId());
        assertEquals("NewP", saved.getName());
    }

    @Test
    void update_nameChangeToExisting_throws() {
        UUID id = UUID.randomUUID();
        ImmunizationPlan existing = new ImmunizationPlan();
        existing.setId(id);
        existing.setName("Old");

        ImmunizationPlan update = new ImmunizationPlan();
        update.setName("Other");

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.existsByName("Other")).thenReturn(true);

        assertThrows(ImmunizationPlanAlreadyExistsException.class, () -> service.updateImmunizationPlan(id, update));
    }

    @Test
    void update_success() throws Exception {
        UUID id = UUID.randomUUID();
        ImmunizationPlan existing = new ImmunizationPlan();
        existing.setId(id);
        existing.setName("Old");

        ImmunizationPlan update = new ImmunizationPlan();
        update.setName("Updated");

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.existsByName("Updated")).thenReturn(false);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        var res = service.updateImmunizationPlan(id, update);
        assertEquals("Updated", res.getName());
    }

    @Test
    void delete_exists_deletes() throws Exception {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);
        service.deleteImmunizationPlan(id);
        verify(repository).deleteById(id);
    }

    @Test
    void delete_notExists_throws() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);
        assertThrows(ImmunizationPlanNotFoundException.class, () -> service.deleteImmunizationPlan(id));
    }

    @Test
    void getByVaccineType_returnsFiltered() {
        UUID vt = UUID.randomUUID();
        ImmunizationPlan p = new ImmunizationPlan();
        p.setVaccineTypeId(vt);
        when(repository.findByVaccineTypeId(vt)).thenReturn(List.of(p));

        var res = service.getImmunizationPlansByVaccineType(vt);
        assertEquals(1, res.size());
    }

    @Test
    void getByAgeCategory_returnsFiltered() {
        UUID ac = UUID.randomUUID();
        ImmunizationPlan p = new ImmunizationPlan();
        p.setAgeCategoryId(ac);
        when(repository.findByAgeCategoryId(ac)).thenReturn(List.of(p));

        var res = service.getImmunizationPlansByAgeCategory(ac);
        assertEquals(1, res.size());
    }
}

