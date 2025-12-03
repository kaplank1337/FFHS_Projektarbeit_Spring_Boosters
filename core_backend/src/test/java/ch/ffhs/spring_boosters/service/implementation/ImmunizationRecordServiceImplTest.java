package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.entity.AgeCategory;
import ch.ffhs.spring_boosters.controller.entity.ImmunizationPlan;
import ch.ffhs.spring_boosters.controller.entity.ImmunizationRecord;
import ch.ffhs.spring_boosters.controller.entity.User;
import ch.ffhs.spring_boosters.repository.AgeCategoryRepository;
import ch.ffhs.spring_boosters.repository.ImmunizationPlanRepository;
import ch.ffhs.spring_boosters.repository.ImmunizationRecordRepository;
import ch.ffhs.spring_boosters.repository.UserRepository;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationRecordNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImmunizationRecordServiceImplTest {

    @Mock
    private ImmunizationRecordRepository recordRepository;

    @Mock
    private ImmunizationPlanRepository planRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AgeCategoryRepository ageCategoryRepository;

    @InjectMocks
    private ImmunizationRecordServiceImpl service;

    @Test
    void getAll_returnsAll() {
        UUID userId = UUID.randomUUID();
        ImmunizationRecord r = new ImmunizationRecord();
        r.setId(UUID.randomUUID());
        r.setUserId(userId);

        when(recordRepository.findAllByUserId(userId)).thenReturn(List.of(r));

        var res = service.getAllImmunizationRecords(userId);
        assertEquals(1, res.size(), "Should return 1 record");
    }

    @Test
    void getById_found() throws Exception {
        UUID id = UUID.randomUUID();
        ImmunizationRecord r = new ImmunizationRecord();
        r.setId(id);

        when(recordRepository.findById(id)).thenReturn(Optional.of(r));
        var found = service.getImmunizationRecordById(id);
        assertEquals(id, found.getId());
    }

    @Test
    void getById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(recordRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ImmunizationRecordNotFoundException.class, () -> service.getImmunizationRecordById(id));
    }

    @Test
    void create_findsPlan_and_saves() {
        UUID userId = UUID.randomUUID();
        UUID vt = UUID.randomUUID();
        UUID ageCategoryId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setBirthDate(LocalDate.of(1990, 1, 1));

        AgeCategory ageCategory = new AgeCategory();
        ageCategory.setId(ageCategoryId);
        ageCategory.setAgeMinDays(10000);
        ageCategory.setAgeMaxDays(15000);

        ImmunizationRecord rec = new ImmunizationRecord();
        rec.setVaccineTypeId(vt);
        rec.setUserId(userId);
        rec.setAdministeredOn(LocalDate.of(2020, 6, 15));

        ImmunizationPlan plan = new ImmunizationPlan();
        plan.setId(UUID.randomUUID());
        plan.setVaccineTypeId(vt);
        plan.setAgeCategoryId(ageCategoryId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ageCategoryRepository.findAll()).thenReturn(List.of(ageCategory));
        when(planRepository.findByVaccineTypeIdAndAgeCategoryId(vt, ageCategoryId)).thenReturn(List.of(plan));
        when(recordRepository.save(any())).thenAnswer(i -> {
            ImmunizationRecord arg = i.getArgument(0);
            arg.setId(UUID.randomUUID());
            return arg;
        });

        var saved = service.createImmunizationRecord(rec);
        assertNotNull(saved.getId(), "Saved record should have an ID");
        assertEquals(plan.getId(), saved.getImmunizationPlanId(), "Should set immunizationPlanId");
    }

    @Test
    void create_noPlan_throws() {
        UUID userId = UUID.randomUUID();
        UUID vt = UUID.randomUUID();
        UUID ageCategoryId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setBirthDate(LocalDate.of(1990, 1, 1));

        AgeCategory ageCategory = new AgeCategory();
        ageCategory.setId(ageCategoryId);
        ageCategory.setAgeMinDays(10000);
        ageCategory.setAgeMaxDays(15000);

        ImmunizationRecord rec = new ImmunizationRecord();
        rec.setVaccineTypeId(vt);
        rec.setUserId(userId);
        rec.setAdministeredOn(LocalDate.of(2020, 6, 15));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ageCategoryRepository.findAll()).thenReturn(List.of(ageCategory));
        when(planRepository.findByVaccineTypeIdAndAgeCategoryId(vt, ageCategoryId)).thenReturn(List.of());

        assertThrows(IllegalStateException.class, () -> service.createImmunizationRecord(rec),
            "Should throw IllegalStateException when no plan is found");
    }

    @Test
    void update_updatesFields() throws Exception {
        UUID id = UUID.randomUUID();
        ImmunizationRecord existing = new ImmunizationRecord();
        existing.setId(id);
        existing.setDoseOrderClaimed(1);

        ImmunizationRecord update = new ImmunizationRecord();
        update.setId(id);
        update.setDoseOrderClaimed(2);
        update.setAdministeredOn(LocalDate.now());

        when(recordRepository.findById(id)).thenReturn(Optional.of(existing));
        when(recordRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var res = service.updateImmunizationRecord(id, update);
        assertEquals(2, res.getDoseOrderClaimed(), "Dose order should be updated to 2");
    }

    @Test
    void delete_found_deletes() throws Exception {
        UUID id = UUID.randomUUID();
        ImmunizationRecord existing = new ImmunizationRecord();
        existing.setId(id);

        when(recordRepository.findById(id)).thenReturn(Optional.of(existing));

        service.deleteImmunizationRecord(UUID.randomUUID(), id);
        verify(recordRepository).delete(existing);
    }

    @Test
    void delete_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(recordRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ImmunizationRecordNotFoundException.class, () -> service.deleteImmunizationRecord(UUID.randomUUID(), id));
    }

    @Test
    void queryMethods_delegatesToRepository() {
        UUID userId = UUID.randomUUID();
        UUID vt = UUID.randomUUID();

        when(recordRepository.findByUserId(userId)).thenReturn(List.of());
        when(recordRepository.findByVaccineTypeId(vt)).thenReturn(List.of());
        when(recordRepository.findByUserIdAndVaccineTypeId(userId, vt)).thenReturn(List.of());

        assertEquals(0, service.getImmunizationRecordsByUser(userId).size());
        assertEquals(0, service.getImmunizationRecordsByVaccineType(vt).size());
        assertEquals(0, service.getImmunizationRecordsByUserAndVaccineType(userId, vt).size());
    }
}

