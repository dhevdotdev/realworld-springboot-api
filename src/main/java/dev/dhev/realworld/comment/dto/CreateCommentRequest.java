package dev.dhev.realworld.comment.dto;

public record CreateCommentRequest(CommentDto comment) {
    public record CommentDto(String body) {}
}
