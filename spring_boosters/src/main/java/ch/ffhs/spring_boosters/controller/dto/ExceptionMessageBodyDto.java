package ch.ffhs.spring_boosters.controller.dto;

import java.time.LocalDateTime;

public record ExceptionMessageBodyDto(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        String exception
) {
    public ExceptionMessageBodyDto(LocalDateTime timestamp, int status, String error, String message, String path) {
        this(timestamp, status, error, message, path, null);
    }
}
