package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.dto.ImmunizationScheduleDto;
import ch.ffhs.spring_boosters.controller.entity.AgeCategory;
import ch.ffhs.spring_boosters.controller.entity.ImmunizationPlan;
import ch.ffhs.spring_boosters.controller.entity.ImmunizationRecord;
import ch.ffhs.spring_boosters.controller.entity.User;
import ch.ffhs.spring_boosters.repository.AgeCategoryRepository;
import ch.ffhs.spring_boosters.repository.ImmunizationPlanRepository;
import ch.ffhs.spring_boosters.repository.ImmunizationRecordRepository;
import ch.ffhs.spring_boosters.repository.UserRepository;
import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;
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
class ImmunizationScheduleServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImmunizationPlanRepository planRepository;

    @Mock
    private ImmunizationRecordRepository recordRepository;

    @Mock
    private AgeCategoryRepository ageCategoryRepository;

    @InjectMocks
    private ImmunizationScheduleServiceImpl service;

    @Test
    void userNotFound_throws() {
        UUID u = UUID.randomUUID();
        when(userRepository.findById(u)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.getPendingImmunizations(u));
    }

    @Test
    void noPlans_returnsEmpty() throws Exception {
        UUID u = UUID.randomUUID();
        User user = new User();
        user.setId(u);
        user.setUsername("u1");
        user.setBirthDate(LocalDate.now().minusDays(100));

        when(userRepository.findById(u)).thenReturn(Optional.of(user));
        when(recordRepository.findByUserId(u)).thenReturn(List.of());
        when(planRepository.findAll()).thenReturn(List.of());
        when(ageCategoryRepository.findAll()).thenReturn(List.of());

        ImmunizationScheduleDto dto = service.getPendingImmunizations(u);
        assertEquals(0, dto.getTotalPending());
    }

    @Test
    void pendingPlan_isDetected() throws Exception {
        UUID u = UUID.randomUUID();
        User user = new User();
        user.setId(u);
        user.setUsername("u1");
        user.setBirthDate(LocalDate.now().minusDays(10));

        AgeCategory cat = new AgeCategory();
        cat.setId(UUID.randomUUID());
        cat.setName("Infant");
        cat.setAgeMinDays(0);
        cat.setAgeMaxDays(365);

        ImmunizationPlan plan = new ImmunizationPlan();
        plan.setId(UUID.randomUUID());
        plan.setName("PlanA");
        plan.setAgeCategoryId(cat.getId());
        plan.setVaccineType(null);

        when(userRepository.findById(u)).thenReturn(Optional.of(user));
        when(recordRepository.findByUserId(u)).thenReturn(List.of());
        when(planRepository.findAll()).thenReturn(List.of(plan));
        when(ageCategoryRepository.findAll()).thenReturn(List.of(cat));

        ImmunizationScheduleDto dto = service.getPendingImmunizations(u);
        assertEquals(1, dto.getTotalPending());
        assertEquals(u, dto.getUserId());
    }
}

