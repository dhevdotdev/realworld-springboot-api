package dev.dhev.realworld.shared.exception;

public class EmailAlreadyTakenException extends AppException {

    public EmailAlreadyTakenException() {
        super("email", "has already been taken");
    }
}
