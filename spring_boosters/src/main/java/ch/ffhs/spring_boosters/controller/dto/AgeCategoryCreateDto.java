package ch.ffhs.spring_boosters.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AgeCategoryCreateDto(
    @NotBlank(message = "Name is required")
    String name,
    @NotNull(message = "Minimum age in days is required")
    Integer ageMinDays,
    Integer ageMaxDays
) {}
