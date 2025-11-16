package ch.ffhs.spring_boosters.controller.mapper;

import ch.ffhs.spring_boosters.controller.dto.ImmunizationPlanCreateDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationPlanDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationPlanUpdateDto;
import ch.ffhs.spring_boosters.controller.entity.ImmunizationPlan;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ImmunizationPlanMapper {

    public ImmunizationPlanDto toDto(ImmunizationPlan immunizationPlan) {
        if (immunizationPlan == null) {
            return null;
        }
        return new ImmunizationPlanDto(
                immunizationPlan.getId(),
                immunizationPlan.getName(),
                immunizationPlan.getVaccineTypeId(),
                immunizationPlan.getAgeCategoryId(),
                immunizationPlan.getCreatedAt(),
                immunizationPlan.getUpdatedAt()
        );
    }

    public List<ImmunizationPlanDto> toDtoList(List<ImmunizationPlan> immunizationPlans) {
        return immunizationPlans.stream()
                .map(this::toDto)
                .toList();
    }

    public ImmunizationPlan fromCreateDto(ImmunizationPlanCreateDto dto) {
        if (dto == null) {
            return null;
        }
        return new ImmunizationPlan(dto.name(), dto.vaccineTypeId(), dto.ageCategoryId());
    }

    public ImmunizationPlan fromUpdateDto(ImmunizationPlanUpdateDto dto) {
        if (dto == null) {
            return null;
        }
        return new ImmunizationPlan(dto.name(), dto.vaccineTypeId(), dto.ageCategoryId());
    }
}
