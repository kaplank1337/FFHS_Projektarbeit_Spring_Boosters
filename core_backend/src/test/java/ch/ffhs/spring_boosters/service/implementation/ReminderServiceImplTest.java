package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class ReminderServiceImplTest {

    @Autowired
    ReminderServiceImpl reminderService;

    @Test
    void should_find_all_reminders() throws UserNotFoundException {
        reminderService.sendReminders();
    }

}