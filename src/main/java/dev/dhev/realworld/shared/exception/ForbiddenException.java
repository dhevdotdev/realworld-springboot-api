package dev.dhev.realworld.shared.exception;

public class ForbiddenException extends AppException {
    public ForbiddenException(String field) {
        super(field, "forbidden");
    }
}
