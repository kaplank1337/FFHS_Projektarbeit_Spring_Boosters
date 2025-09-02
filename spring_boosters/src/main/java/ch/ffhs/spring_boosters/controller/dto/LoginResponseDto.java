package ch.ffhs.spring_boosters.controller.dto;

public record LoginResponseDto(
        boolean success,
        String message,
        String userName
) {
}
