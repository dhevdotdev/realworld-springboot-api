package dev.dhev.realworld.user.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.dhev.realworld.shared.OptionalStringDeserializer;

import java.util.Optional;

public record UpdateUserRequest(UserUpdateDto user) {
    public record UserUpdateDto(
            @JsonDeserialize(using = OptionalStringDeserializer.class) Optional<String> email,
            @JsonDeserialize(using = OptionalStringDeserializer.class) Optional<String> username,
            String password,
            @JsonDeserialize(using = OptionalStringDeserializer.class) Optional<String> bio,
            @JsonDeserialize(using = OptionalStringDeserializer.class) Optional<String> image
    ) {}
}
