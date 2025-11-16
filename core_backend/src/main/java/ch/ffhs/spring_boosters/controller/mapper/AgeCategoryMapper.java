package ch.ffhs.spring_boosters.controller.mapper;

import ch.ffhs.spring_boosters.controller.dto.AgeCategoryCreateDto;
import ch.ffhs.spring_boosters.controller.dto.AgeCategoryDto;
import ch.ffhs.spring_boosters.controller.dto.AgeCategoryUpdateDto;
import ch.ffhs.spring_boosters.controller.entity.AgeCategory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AgeCategoryMapper {

    public AgeCategoryDto toDto(AgeCategory ageCategory) {
        if (ageCategory == null) {
            return null;
        }
        return new AgeCategoryDto(
                ageCategory.getId(),
                ageCategory.getName(),
                ageCategory.getAgeMinDays(),
                ageCategory.getAgeMaxDays(),
                ageCategory.getCreatedAt(),
                ageCategory.getUpdatedAt()
        );
    }

    public List<AgeCategoryDto> toDtoList(List<AgeCategory> ageCategories) {
        return ageCategories.stream()
                .map(this::toDto)
                .toList();
    }

    public AgeCategory fromCreateDto(AgeCategoryCreateDto dto) {
        if (dto == null) {
            return null;
        }
        return new AgeCategory(dto.name(), dto.ageMinDays(), dto.ageMaxDays());
    }

    public AgeCategory fromUpdateDto(AgeCategoryUpdateDto dto) {
        if (dto == null) {
            return null;
        }
        return new AgeCategory(dto.name(), dto.ageMinDays(), dto.ageMaxDays());
    }
}
