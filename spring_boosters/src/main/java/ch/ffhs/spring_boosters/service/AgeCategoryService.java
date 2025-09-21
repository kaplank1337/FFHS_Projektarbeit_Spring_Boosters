package ch.ffhs.spring_boosters.service;

import ch.ffhs.spring_boosters.controller.entity.AgeCategory;
import ch.ffhs.spring_boosters.service.Exception.AgeCategoryAlreadyExistsException;
import ch.ffhs.spring_boosters.service.Exception.AgeCategoryNotFoundException;

import java.util.List;
import java.util.UUID;

public interface AgeCategoryService {
    List<AgeCategory> getAllAgeCategories();
    AgeCategory getAgeCategoryById(UUID id) throws AgeCategoryNotFoundException;
    AgeCategory createAgeCategory(AgeCategory ageCategory) throws AgeCategoryAlreadyExistsException;
    AgeCategory updateAgeCategory(UUID id, AgeCategory ageCategory) throws AgeCategoryNotFoundException, AgeCategoryAlreadyExistsException;
    void deleteAgeCategory(UUID id) throws AgeCategoryNotFoundException;
}
