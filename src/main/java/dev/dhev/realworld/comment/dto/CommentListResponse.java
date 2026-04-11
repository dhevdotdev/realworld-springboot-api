package dev.dhev.realworld.comment.dto;

import dev.dhev.realworld.comment.Comment;
import dev.dhev.realworld.shared.InstantPrecision;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record CommentListResponse(List<CommentItemDto> comments) {

    public record CommentItemDto(
        Long id,
        String body,
        Instant createdAt,
        Instant updatedAt,
        CommentResponse.AuthorDto author
    ) {}

    public static CommentListResponse from(List<Comment> comments, boolean[] followingFlags) {
        var items = new ArrayList<CommentItemDto>(comments.size());
        for (int i = 0; i < comments.size(); i++) {
            var c = comments.get(i);
            var author = new CommentResponse.AuthorDto(
                c.getAuthor().getUsername(),
                c.getAuthor().getBio(),
                c.getAuthor().getImage(),
                followingFlags[i]
            );
            items.add(new CommentItemDto(
                c.getId(),
                c.getBody(),
                InstantPrecision.toMicros(c.getCreatedAt()),
                InstantPrecision.toMicros(c.getUpdatedAt()),
                author
            ));
        }
        return new CommentListResponse(items);
    }
}
