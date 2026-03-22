package dev.dhev.realworld.user.dto;

public record LoginUserRequest(UserLoginDto user) {
    public record UserLoginDto(String email, String password) {}
}
