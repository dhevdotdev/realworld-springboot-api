package dev.dhev.realworld.user.dto;

public record UserResponse(UserDto user) {
    public record UserDto(String email, String token, String username, String bio, String image) {}

    public static UserResponse from(dev.dhev.realworld.user.User user, String token) {
        return new UserResponse(new UserDto(user.getEmail(), token, user.getUsername(), user.getBio(), user.getImage()));
    }
}
