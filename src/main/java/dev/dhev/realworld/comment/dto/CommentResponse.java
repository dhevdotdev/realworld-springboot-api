package dev.dhev.realworld.comment.dto;

import dev.dhev.realworld.comment.Comment;

import java.time.Instant;

public record CommentResponse(CommentDto comment) {

    public record CommentDto(
        Long id,
        String body,
        Instant createdAt,
        Instant updatedAt,
        AuthorDto author
    ) {}

    public record AuthorDto(String username, String bio, String image, boolean following) {}

    public static CommentResponse from(Comment comment, boolean following) {
        var author = new AuthorDto(
            comment.getAuthor().getUsername(),
            comment.getAuthor().getBio(),
            comment.getAuthor().getImage(),
            following
        );
        return new CommentResponse(new CommentDto(
            comment.getId(),
            comment.getBody(),
            comment.getCreatedAt(),
            comment.getUpdatedAt(),
            author
        ));
    }
}
