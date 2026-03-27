package dev.dhev.realworld.profile;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public record FollowId(Long followerId, Long followingId) implements Serializable {}
