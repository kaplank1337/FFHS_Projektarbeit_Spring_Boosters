package ch.ffhs.spring_boosters.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record ActiveSubstanceCreateDto(
    @NotBlank(message = "Name is required")
    String name,
    String[] synonyms
) {}
