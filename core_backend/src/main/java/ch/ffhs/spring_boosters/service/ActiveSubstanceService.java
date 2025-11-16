package ch.ffhs.spring_boosters.service;

import ch.ffhs.spring_boosters.controller.entity.ActiveSubstance;
import ch.ffhs.spring_boosters.service.Exception.ActiveSubstanceAlreadyExistsException;
import ch.ffhs.spring_boosters.service.Exception.ActiveSubstanceNotFoundException;

import java.util.List;
import java.util.UUID;

public interface ActiveSubstanceService {
    List<ActiveSubstance> getAllActiveSubstances();
    ActiveSubstance getActiveSubstanceById(UUID id) throws ActiveSubstanceNotFoundException;
    ActiveSubstance createActiveSubstance(ActiveSubstance activeSubstance) throws ActiveSubstanceAlreadyExistsException;
    ActiveSubstance updateActiveSubstance(UUID id, ActiveSubstance activeSubstance) throws ActiveSubstanceNotFoundException, ActiveSubstanceAlreadyExistsException;
    void deleteActiveSubstance(UUID id) throws ActiveSubstanceNotFoundException;
}
