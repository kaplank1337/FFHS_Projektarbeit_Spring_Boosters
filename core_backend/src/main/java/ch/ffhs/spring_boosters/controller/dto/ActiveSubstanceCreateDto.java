package ch.ffhs.spring_boosters.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ActiveSubstanceCreateDto(
    @NotBlank(message = "Name is required")
    @NotNull
    String name,
    String[] synonyms
) {}
