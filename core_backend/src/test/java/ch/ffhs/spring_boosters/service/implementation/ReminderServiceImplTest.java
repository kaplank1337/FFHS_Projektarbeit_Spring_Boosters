package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.dto.ImmunizationScheduleDto;
import ch.ffhs.spring_boosters.controller.dto.NotificationEmailRequestDto;
import ch.ffhs.spring_boosters.controller.dto.PendingImmunizationDto;
import ch.ffhs.spring_boosters.controller.entity.User;
import ch.ffhs.spring_boosters.repository.UserRepository;
import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReminderServiceImplTest {

    private UserRepository userRepository;
    private ImmunizationScheduleServiceImpl immunizationScheduleService;
    private RestTemplate restTemplate;

    private ReminderServiceImpl reminderService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        immunizationScheduleService = Mockito.mock(ImmunizationScheduleServiceImpl.class);
        restTemplate = Mockito.mock(RestTemplate.class);

        reminderService = new ReminderServiceImpl(
                userRepository,
                immunizationScheduleService,
                restTemplate,
                "notification-service-host",
                8082,
                "/api/v1/email/send"
        );
    }

    @Test
    void sendReminders_throwsUserNotFoundException_whenNoUsersFound() {
        when(userRepository.findAllByRole("USER")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> reminderService.sendReminders());

        verify(userRepository, times(1)).findAllByRole("USER");
        verifyNoInteractions(immunizationScheduleService, restTemplate);
    }

    @Test
    void sendReminders_skipsUser_whenScheduleIsNull() throws Exception {
        User user = createUser();
        when(userRepository.findAllByRole("USER")).thenReturn(Optional.of(List.of(user)));
        when(immunizationScheduleService.getPendingImmunizations(user.getId()))
                .thenReturn(null);

        reminderService.sendReminders();

        verify(immunizationScheduleService, times(1)).getPendingImmunizations(user.getId());
        verifyNoInteractions(restTemplate);
    }

    @Test
    void sendReminders_skipsUser_whenPendingListIsNull() throws Exception {
        User user = createUser();
        when(userRepository.findAllByRole("USER")).thenReturn(Optional.of(List.of(user)));

        ImmunizationScheduleDto schedule = new ImmunizationScheduleDto();
        schedule.setPendingImmunizations(null);

        when(immunizationScheduleService.getPendingImmunizations(user.getId()))
                .thenReturn(schedule);

        reminderService.sendReminders();

        verify(immunizationScheduleService, times(1)).getPendingImmunizations(user.getId());
        verifyNoInteractions(restTemplate);
    }

    @Test
    void sendReminders_skipsUser_whenPendingListIsEmpty() throws Exception {
        User user = createUser();
        when(userRepository.findAllByRole("USER")).thenReturn(Optional.of(List.of(user)));

        ImmunizationScheduleDto schedule = new ImmunizationScheduleDto();
        schedule.setPendingImmunizations(Collections.emptyList());

        when(immunizationScheduleService.getPendingImmunizations(user.getId()))
                .thenReturn(schedule);

        reminderService.sendReminders();

        verify(immunizationScheduleService, times(1)).getPendingImmunizations(user.getId());
        verifyNoInteractions(restTemplate);
    }

    @Test
    void sendReminders_skipsUser_whenTotalPendingIsZero() throws Exception {
        User user = createUser();
        when(userRepository.findAllByRole("USER")).thenReturn(Optional.of(List.of(user)));

        ImmunizationScheduleDto schedule = new ImmunizationScheduleDto();
        schedule.setPendingImmunizations(List.of(createPendingImmunization()));
        schedule.setTotalPending(0);

        when(immunizationScheduleService.getPendingImmunizations(user.getId()))
                .thenReturn(schedule);

        reminderService.sendReminders();

        verify(immunizationScheduleService, times(1)).getPendingImmunizations(user.getId());
        verifyNoInteractions(restTemplate);
    }

    @Test
    void sendReminders_logsError_whenRestTemplateThrowsException() throws Exception {
        User user = createUser();
        when(userRepository.findAllByRole("USER")).thenReturn(Optional.of(List.of(user)));

        ImmunizationScheduleDto schedule = new ImmunizationScheduleDto();
        schedule.setPendingImmunizations(List.of(createPendingImmunization()));
        schedule.setTotalPending(1);

        when(immunizationScheduleService.getPendingImmunizations(user.getId()))
                .thenReturn(schedule);

        doThrow(new RuntimeException("Test error"))
                .when(restTemplate).postForEntity(any(String.class), any(NotificationEmailRequestDto.class), eq(Void.class));

        reminderService.sendReminders();

        verify(restTemplate, times(1))
                .postForEntity(any(String.class), any(NotificationEmailRequestDto.class), eq(Void.class));
    }

    private User createUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("user@example.com");
        user.setUsername("testuser");
        return user;
    }

    private PendingImmunizationDto createPendingImmunization() {
        PendingImmunizationDto pending = new PendingImmunizationDto();
        pending.setVaccineTypeName("Test Vaccine");
        pending.setDueDate(LocalDate.now());
        pending.setPriority("overdue");
        pending.setReason("Reason");
        return pending;
    }
}