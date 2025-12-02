package ch.ffhs.spring_boosters.controller.dto;


import java.util.List;

public record ImmunizationSchedulRecordSortedByPriorityDto(
        List<String> vaccinationNames
) {

}
