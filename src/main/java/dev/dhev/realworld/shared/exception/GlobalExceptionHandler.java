package dev.dhev.realworld.shared.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EmailAlreadyTakenException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Map<String, List<String>>> handleEmailTaken(EmailAlreadyTakenException ex) {
        return error(ex);
    }

    @ExceptionHandler(UsernameAlreadyTakenException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Map<String, List<String>>> handleUsernameTaken(UsernameAlreadyTakenException ex) {
        return error(ex);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Map<String, List<String>>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return error(ex);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Map<String, List<String>>> handleNotFound(ResourceNotFoundException ex) {
        return error(ex);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Map<String, Map<String, List<String>>> handleValidation(ValidationException ex) {
        return error(ex);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Map<String, List<String>>> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return Map.of("errors", Map.of("server", List.of("an unexpected error occurred")));
    }

    private Map<String, Map<String, List<String>>> error(AppException ex) {
        return Map.of("errors", Map.of(ex.getField(), List.of(ex.getMessage())));
    }
}
