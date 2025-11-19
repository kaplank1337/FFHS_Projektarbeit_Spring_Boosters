package ch.ffhs.spring_boosters.service;

import ch.ffhs.spring_boosters.controller.dto.ImmunizationScheduleDto;
import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;

import java.util.UUID;

public interface ImmunizationScheduleService {
    /**
     * Berechnet die ausstehenden Impfungen für einen Benutzer
     * basierend auf Alter, bereits erfassten Impfungen und Impfplänen
     */
    ImmunizationScheduleDto getPendingImmunizations(UUID userId) throws UserNotFoundException;
}

