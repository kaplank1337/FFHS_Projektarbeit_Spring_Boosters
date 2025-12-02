package ch.ffhs.spring_boosters.controller.dto;


import ch.ffhs.spring_boosters.service.implementation.enumerator.PriorityEnum;
import jakarta.validation.constraints.Pattern;

public record PriorityRequestDto(
        @Pattern(
                regexp = "overdue|due-soon|upcoming",
                flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "priority must be one of: overdue, due-soon, upcoming"
        )
        String value
) {
    public PriorityEnum toEnum() {
        return PriorityEnum.fromValue(value);
    }
}