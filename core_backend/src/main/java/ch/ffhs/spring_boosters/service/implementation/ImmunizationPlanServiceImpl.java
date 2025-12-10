package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.entity.ImmunizationPlan;
import ch.ffhs.spring_boosters.repository.ImmunizationPlanRepository;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationPlanAlreadyExistsException;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationPlanNotFoundException;
import ch.ffhs.spring_boosters.service.ImmunizationPlanService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ImmunizationPlanServiceImpl implements ImmunizationPlanService {

    private final ImmunizationPlanRepository immunizationPlanRepository;

    @Override
    public List<ImmunizationPlan> getAllImmunizationPlans() {
        return immunizationPlanRepository.findAll();
    }

    @Override
    public ImmunizationPlan getImmunizationPlanById(UUID id) throws ImmunizationPlanNotFoundException {
        return immunizationPlanRepository.findById(id)
                .orElseThrow(() -> new ImmunizationPlanNotFoundException("Immunization plan with id " + id + " not found"));
    }

    @Override
    public ImmunizationPlan createImmunizationPlan(ImmunizationPlan immunizationPlan) throws ImmunizationPlanAlreadyExistsException {
        if (immunizationPlanRepository.existsByName(immunizationPlan.getName())) {
            throw new ImmunizationPlanAlreadyExistsException("Immunization plan with name '" + immunizationPlan.getName() + "' already exists");
        }
        return immunizationPlanRepository.save(immunizationPlan);
    }

    @Override
    public ImmunizationPlan updateImmunizationPlan(UUID id, ImmunizationPlan immunizationPlan) throws ImmunizationPlanNotFoundException, ImmunizationPlanAlreadyExistsException {
        ImmunizationPlan existingPlan = getImmunizationPlanById(id);

        // Check if name is being changed and if new name already exists
        if (!existingPlan.getName().equals(immunizationPlan.getName())
            && immunizationPlanRepository.existsByName(immunizationPlan.getName())) {
            throw new ImmunizationPlanAlreadyExistsException("Immunization plan with name '" + immunizationPlan.getName() + "' already exists");
        }

        existingPlan.setName(immunizationPlan.getName());
        existingPlan.setVaccineTypeId(immunizationPlan.getVaccineTypeId());
        existingPlan.setAgeCategoryId(immunizationPlan.getAgeCategoryId());

        return immunizationPlanRepository.save(existingPlan);
    }

    @Override
    public void deleteImmunizationPlan(UUID id) throws ImmunizationPlanNotFoundException {
        if (!immunizationPlanRepository.existsById(id)) {
            throw new ImmunizationPlanNotFoundException("Immunization plan with id " + id + " not found");
        }
        immunizationPlanRepository.deleteById(id);
    }

    @Override
    public List<ImmunizationPlan> getImmunizationPlansByVaccineType(UUID vaccineTypeId) {
        return immunizationPlanRepository.findByVaccineTypeId(vaccineTypeId);
    }

    @Override
    public List<ImmunizationPlan> getImmunizationPlansByAgeCategory(UUID ageCategoryId) {
        return immunizationPlanRepository.findByAgeCategoryId(ageCategoryId);
    }
}
