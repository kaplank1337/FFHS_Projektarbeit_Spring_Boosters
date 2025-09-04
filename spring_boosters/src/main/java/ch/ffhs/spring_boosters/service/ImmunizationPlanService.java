package ch.ffhs.spring_boosters.service;

import ch.ffhs.spring_boosters.controller.entity.ImmunizationPlan;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationPlanAlreadyExistsException;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationPlanNotFoundException;

import java.util.List;
import java.util.UUID;

public interface ImmunizationPlanService {
    List<ImmunizationPlan> getAllImmunizationPlans();
    ImmunizationPlan getImmunizationPlanById(UUID id) throws ImmunizationPlanNotFoundException;
    ImmunizationPlan createImmunizationPlan(ImmunizationPlan immunizationPlan) throws ImmunizationPlanAlreadyExistsException;
    ImmunizationPlan updateImmunizationPlan(UUID id, ImmunizationPlan immunizationPlan) throws ImmunizationPlanNotFoundException, ImmunizationPlanAlreadyExistsException;
    void deleteImmunizationPlan(UUID id) throws ImmunizationPlanNotFoundException;
    List<ImmunizationPlan> getImmunizationPlansByVaccineType(UUID vaccineTypeId);
    List<ImmunizationPlan> getImmunizationPlansByAgeCategory(UUID ageCategoryId);
}
