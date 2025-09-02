package ch.ffhs.spring_boosters.controller.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UserDto(
    UUID id,
    String username,
    String firstName,
    String lastName,
    LocalDate birthDate,
    String role,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
