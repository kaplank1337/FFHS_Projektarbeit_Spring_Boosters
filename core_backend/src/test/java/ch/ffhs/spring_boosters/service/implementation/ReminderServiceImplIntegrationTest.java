package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Diese KLasse wurde genutzt um E-Mail Erinnerungen in der lokalen Umgebung zu testen.
 */
@SpringBootTest
@ActiveProfiles("local")
class ReminderServiceImplIntegrationTest {

    @Autowired
    ReminderServiceImpl reminderService;

    @Test
    @Disabled
    void should_find_all_reminders() throws UserNotFoundException {
        reminderService.sendReminders();
    }
}