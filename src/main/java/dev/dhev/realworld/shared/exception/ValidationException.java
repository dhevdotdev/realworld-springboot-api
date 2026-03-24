package dev.dhev.realworld.shared.exception;

public class ValidationException extends AppException {

    public ValidationException(String field, String message) {
        super(field, message);
    }
}
