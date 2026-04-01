package dev.dhev.realworld.article.dto;

import dev.dhev.realworld.article.Article;
import dev.dhev.realworld.article.Tag;

import java.time.Instant;
import java.util.List;

public record ArticleResponse(ArticleDto article) {

    public record ArticleDto(
        String slug,
        String title,
        String description,
        String body,
        List<String> tagList,
        Instant createdAt,
        Instant updatedAt,
        boolean favorited,
        long favoritesCount,
        AuthorDto author
    ) {}

    public record AuthorDto(String username, String bio, String image, boolean following) {}

    public static ArticleResponse from(Article article, boolean favorited, boolean following, long favoritesCount) {
        var author = new AuthorDto(
            article.getAuthor().getUsername(),
            article.getAuthor().getBio(),
            article.getAuthor().getImage(),
            following
        );
        var dto = new ArticleDto(
            article.getSlug(),
            article.getTitle(),
            article.getDescription(),
            article.getBody(),
            article.getTags().stream().map(Tag::getName).toList(),
            article.getCreatedAt(),
            article.getUpdatedAt(),
            favorited,
            favoritesCount,
            author
        );
        return new ArticleResponse(dto);
    }
}
