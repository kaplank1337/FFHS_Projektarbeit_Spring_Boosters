package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.entity.AgeCategory;
import ch.ffhs.spring_boosters.repository.AgeCategoryRepository;
import ch.ffhs.spring_boosters.service.AgeCategoryService;
import ch.ffhs.spring_boosters.service.Exception.AgeCategoryAlreadyExistsException;
import ch.ffhs.spring_boosters.service.Exception.AgeCategoryNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AgeCategoryServiceImpl implements AgeCategoryService {

    private final AgeCategoryRepository ageCategoryRepository;

    @Override
    public List<AgeCategory> getAllAgeCategories() {
        return ageCategoryRepository.findAll();
    }

    @Override
    public AgeCategory getAgeCategoryById(UUID id) throws AgeCategoryNotFoundException {
        return ageCategoryRepository.findById(id)
                .orElseThrow(() -> new AgeCategoryNotFoundException("Age category with id " + id + " not found"));
    }

    @Override
    public AgeCategory createAgeCategory(AgeCategory ageCategory) throws AgeCategoryAlreadyExistsException {
        if (ageCategoryRepository.existsByName(ageCategory.getName())) {
            throw new AgeCategoryAlreadyExistsException("Age category with name '" + ageCategory.getName() + "' already exists");
        }
        return ageCategoryRepository.save(ageCategory);
    }

    @Override
    public AgeCategory updateAgeCategory(UUID id, AgeCategory ageCategory) throws AgeCategoryNotFoundException, AgeCategoryAlreadyExistsException {
        AgeCategory existingAgeCategory = getAgeCategoryById(id);

        // Check if name is being changed and if new name already exists
        if (!existingAgeCategory.getName().equals(ageCategory.getName())
            && ageCategoryRepository.existsByName(ageCategory.getName())) {
            throw new AgeCategoryAlreadyExistsException("Age category with name '" + ageCategory.getName() + "' already exists");
        }

        existingAgeCategory.setName(ageCategory.getName());
        existingAgeCategory.setAgeMinDays(ageCategory.getAgeMinDays());
        existingAgeCategory.setAgeMaxDays(ageCategory.getAgeMaxDays());

        return ageCategoryRepository.save(existingAgeCategory);
    }

    @Override
    public void deleteAgeCategory(UUID id) throws AgeCategoryNotFoundException {
        if (!ageCategoryRepository.existsById(id)) {
            throw new AgeCategoryNotFoundException("Age category with id " + id + " not found");
        }
        ageCategoryRepository.deleteById(id);
    }
}
