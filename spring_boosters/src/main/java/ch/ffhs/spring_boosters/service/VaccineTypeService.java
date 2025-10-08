package ch.ffhs.spring_boosters.service;

import ch.ffhs.spring_boosters.controller.entity.VaccineType;
import ch.ffhs.spring_boosters.service.Exception.VaccineTypeNotFoundException;

import java.util.List;
import java.util.UUID;

public interface VaccineTypeService {

    List<VaccineType> getVaccineTypes();
    VaccineType getVaccineType(UUID id) throws VaccineTypeNotFoundException;
}
