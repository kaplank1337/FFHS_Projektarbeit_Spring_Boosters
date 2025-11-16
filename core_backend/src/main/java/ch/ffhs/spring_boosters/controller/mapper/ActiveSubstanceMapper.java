package ch.ffhs.spring_boosters.controller.mapper;

import ch.ffhs.spring_boosters.controller.dto.ActiveSubstanceCreateDto;
import ch.ffhs.spring_boosters.controller.dto.ActiveSubstanceDto;
import ch.ffhs.spring_boosters.controller.dto.ActiveSubstanceUpdateDto;
import ch.ffhs.spring_boosters.controller.entity.ActiveSubstance;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActiveSubstanceMapper {

    public ActiveSubstanceDto toDto(ActiveSubstance activeSubstance) {
        if (activeSubstance == null) {
            return null;
        }
        return new ActiveSubstanceDto(
                activeSubstance.getId(),
                activeSubstance.getName(),
                activeSubstance.getSynonyms(),
                activeSubstance.getCreatedAt(),
                activeSubstance.getUpdatedAt()
        );
    }

    public List<ActiveSubstanceDto> toDtoList(List<ActiveSubstance> activeSubstances) {
        return activeSubstances.stream()
                .map(this::toDto)
                .toList();
    }

    public ActiveSubstance fromCreateDto(ActiveSubstanceCreateDto dto) {
        if (dto == null) {
            return null;
        }
        return new ActiveSubstance(dto.name(), dto.synonyms());
    }

    public ActiveSubstance fromUpdateDto(ActiveSubstanceUpdateDto dto) {
        if (dto == null) {
            return null;
        }
        return new ActiveSubstance(dto.name(), dto.synonyms());
    }
}
