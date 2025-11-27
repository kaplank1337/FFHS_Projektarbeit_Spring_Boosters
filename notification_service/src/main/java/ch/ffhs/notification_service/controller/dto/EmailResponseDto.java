package ch.ffhs.notification_service.controller.dto;

public record EmailResponseDto(
        boolean success,
        String message,
        String timestamp
) {}

