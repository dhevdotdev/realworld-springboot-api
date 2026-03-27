package dev.dhev.realworld.profile.dto;

import dev.dhev.realworld.user.User;

public record ProfileResponse(ProfileDto profile) {

    public record ProfileDto(String username, String bio, String image, boolean following) {}

    public static ProfileResponse from(User user, boolean following) {
        return new ProfileResponse(new ProfileDto(user.getUsername(), user.getBio(), user.getImage(), following));
    }
}
