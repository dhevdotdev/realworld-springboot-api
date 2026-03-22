package dev.dhev.realworld.shared.exception;

public abstract class AppException extends RuntimeException {

    private final String field;

    protected AppException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
