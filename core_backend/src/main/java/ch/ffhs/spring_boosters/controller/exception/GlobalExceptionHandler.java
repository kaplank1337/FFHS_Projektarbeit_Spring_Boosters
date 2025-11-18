package ch.ffhs.spring_boosters.controller.exception;

import ch.ffhs.spring_boosters.controller.dto.ExceptionMessageBodyDto;
import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionMessageBodyDto> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Validation failed");

        ExceptionMessageBodyDto errorResponse = new ExceptionMessageBodyDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                errorMessage,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionMessageBodyDto> handleTypeMismatchException(
            Exception ex,
            HttpServletRequest request) {

        ExceptionMessageBodyDto errorResponse = new ExceptionMessageBodyDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Internal Server Error",
                "An unexpected error occurred: " + ex.getMessage(),
                request.getRequestURI(),
                ex.getClass().getSimpleName()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionMessageBodyDto> handleUserNotFoundException(
            Exception ex,
            HttpServletRequest request) {

        ExceptionMessageBodyDto errorResponse = new ExceptionMessageBodyDto(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "User not found",
                "An unexpected error occurred: " + ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionMessageBodyDto> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        ExceptionMessageBodyDto errorResponse = new ExceptionMessageBodyDto(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred: " + ex.getMessage(),
                request.getRequestURI(),
                ex.getClass().getSimpleName()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
