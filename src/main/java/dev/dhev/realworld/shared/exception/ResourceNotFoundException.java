package dev.dhev.realworld.shared.exception;

public class ResourceNotFoundException extends AppException {

    public ResourceNotFoundException(String resource) {
        super(resource, "not found");
    }
}
