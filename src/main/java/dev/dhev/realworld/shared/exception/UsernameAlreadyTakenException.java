package dev.dhev.realworld.shared.exception;

public class UsernameAlreadyTakenException extends AppException {

    public UsernameAlreadyTakenException() {
        super("username", "has already been taken");
    }
}
