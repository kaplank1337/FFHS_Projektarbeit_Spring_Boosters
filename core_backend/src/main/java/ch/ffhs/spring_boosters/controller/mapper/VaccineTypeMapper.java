package ch.ffhs.spring_boosters.controller.mapper;

import ch.ffhs.spring_boosters.controller.dto.VaccineTypeDto;
import ch.ffhs.spring_boosters.controller.dto.VaccineTypeListDto;
import ch.ffhs.spring_boosters.controller.entity.VaccineType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class VaccineTypeMapper {

    public VaccineTypeListDto vaccineTypeListDto(List<VaccineType> vaccineTypes) {
        ArrayList<VaccineTypeDto> vaccineTypeDtos = new ArrayList<>();
        for (VaccineType vaccineType : vaccineTypes) {
            vaccineTypeDtos.add(vaccineTypeToDto(vaccineType));
        }
        return new VaccineTypeListDto(vaccineTypeDtos);
    }

    public VaccineTypeDto vaccineTypeToDto(VaccineType vaccineType) {
        if (vaccineType == null) {
            return null;
        }
        return new VaccineTypeDto(
                vaccineType.getName(),
                vaccineType.getCode(),
                vaccineType.getVaccineTypeActiveSubstances()
        );
    }
}
