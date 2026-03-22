package dev.dhev.realworld.shared.exception;

public class InvalidCredentialsException extends AppException {

    public InvalidCredentialsException() {
        super("credentials", "is invalid");
    }
}
