package ch.ffhs.spring_boosters.controller.dto;

import java.util.List;

public record VaccineTypeListDto(
        List<VaccineTypeDto> vaccineTypeDtoList
) {
}
