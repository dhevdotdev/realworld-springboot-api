package dev.dhev.realworld.article.dto;

import dev.dhev.realworld.article.Article;

import java.time.Instant;
import java.util.List;

public record ArticleListResponse(List<ArticleListItemDto> articles, long articlesCount) {

    public record ArticleListItemDto(
        String slug,
        String title,
        String description,
        List<String> tagList,
        Instant createdAt,
        Instant updatedAt,
        boolean favorited,
        long favoritesCount,
        ArticleResponse.AuthorDto author
    ) {}

    public static ArticleListItemDto toListItem(Article article, boolean favorited, boolean following) {
        var author = new ArticleResponse.AuthorDto(
            article.getAuthor().getUsername(),
            article.getAuthor().getBio(),
            article.getAuthor().getImage(),
            following
        );
        return new ArticleListItemDto(
            article.getSlug(),
            article.getTitle(),
            article.getDescription(),
            article.getTags().stream().map(t -> t.getName()).toList(),
            article.getCreatedAt(),
            article.getUpdatedAt(),
            favorited,
            article.getFavoritedBy().size(),
            author
        );
    }
}
