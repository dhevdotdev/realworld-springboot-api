package dev.dhev.realworld.user.dto;

public record UpdateUserRequest(UserUpdateDto user) {
    public record UserUpdateDto(String email, String username, String password, String bio, String image) {}
}
