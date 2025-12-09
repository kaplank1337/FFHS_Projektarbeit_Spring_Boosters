package ch.ffhs.spring_boosters.service;

import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;

public interface ReminderService {

    void sendReminders() throws UserNotFoundException;
}
