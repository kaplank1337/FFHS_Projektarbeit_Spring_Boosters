package ch.ffhs.spring_boosters.controller.dto;

import java.time.LocalDate;
import java.util.UUID;

public record UserDto(
    UUID id,
    String username,
    String email,
    String firstName,
    String lastName,
    LocalDate birthDate,
    String role
) {}
