package ch.ffhs.notification_service.dto;

public record EmailResponseDto(
        boolean success,
        String message,
        String timestamp
) {}

