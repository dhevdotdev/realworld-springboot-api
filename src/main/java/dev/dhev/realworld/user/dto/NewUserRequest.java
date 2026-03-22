package dev.dhev.realworld.user.dto;

public record NewUserRequest(UserRegistrationDto user) {
    public record UserRegistrationDto(String username, String email, String password) {}
}
