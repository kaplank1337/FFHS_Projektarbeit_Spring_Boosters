package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.dto.ImmunizationScheduleDto;
import ch.ffhs.spring_boosters.controller.dto.NotificationEmailRequestDto;
import ch.ffhs.spring_boosters.controller.dto.NotificationVaccinationDto;
import ch.ffhs.spring_boosters.controller.dto.PendingImmunizationDto;
import ch.ffhs.spring_boosters.controller.entity.User;
import ch.ffhs.spring_boosters.repository.UserRepository;
import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;
import ch.ffhs.spring_boosters.service.ReminderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class ReminderServiceImpl implements ReminderService {

    private final UserRepository userRepository;
    private final ImmunizationScheduleServiceImpl immunizationScheduleService;
    private final RestTemplate restTemplate;

    private final String notificationServiceUrl;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Value("${reminder.cron.expression:0 0 10 ? * SUN}")
    private String reminderCronExpression;

    public ReminderServiceImpl(
            UserRepository userRepository,
            ImmunizationScheduleServiceImpl immunizationScheduleService,
            RestTemplate restTemplate,
            @Value("${notification.service.host}") String host,
            @Value("${notification.service.port}") int port,
            @Value("${notification.service.path}") String path
    ) {
        this.userRepository = userRepository;
        this.immunizationScheduleService = immunizationScheduleService;
        this.restTemplate = restTemplate;
        this.notificationServiceUrl = "http://" + host + ":" + port + path;
    }

    @Override
    @Scheduled(cron = "${reminder.cron.expression:0 0 10 ? * SUN}")
    public void sendReminders() throws UserNotFoundException {

        List<User> users = userRepository.findAllByRole("USER")
                .orElseThrow(() -> new UserNotFoundException("No users with role USER found"));

        for (User user : users) {
            ImmunizationScheduleDto schedule =
                    immunizationScheduleService.getPendingImmunizations(user.getId());

            if (schedule == null || schedule.getPendingImmunizations() == null
                    || schedule.getPendingImmunizations().isEmpty()) {
                continue;
            }

            if (schedule.getTotalPending() != null && schedule.getTotalPending() == 0) {
                continue;
            }

            NotificationEmailRequestDto emailRequest = mapToEmailRequest(user, schedule);

            try {
                restTemplate.postForEntity(notificationServiceUrl, emailRequest, Void.class);
                log.info("Reminder-E-Mail fuer User {} ({}) gesendet",
                        user.getId(), user.getEmail());
            } catch (Exception ex) {
                log.error("Fehler beim Senden der Reminder-E-Mail fuer User {}: {}",
                        user.getId(), ex.getMessage(), ex);
            }
        }
    }

    private NotificationEmailRequestDto mapToEmailRequest(User user, ImmunizationScheduleDto schedule) {

        String subject = "Impf-Erinnerung \u2013 ausstehende Impfungen";

        List<NotificationVaccinationDto> vaccinations = schedule.getPendingImmunizations().stream()
                .map(this::mapToNotificationVaccination)
                .toList();

        return new NotificationEmailRequestDto(
                user.getEmail(),
                user.getUsername(),
                subject,
                vaccinations
        );
    }

    private NotificationVaccinationDto mapToNotificationVaccination(PendingImmunizationDto pending) {

        String vaccineName = pending.getVaccineTypeName();
        String dueDate = pending.getDueDate().toString();
        String status = pending.getPriority();
        String description = pending.getReason();

        return new NotificationVaccinationDto(
                vaccineName,
                dueDate,
                status,
                description
        );
    }
}
