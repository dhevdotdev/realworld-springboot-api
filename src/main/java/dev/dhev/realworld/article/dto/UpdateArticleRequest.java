package dev.dhev.realworld.article.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.dhev.realworld.shared.OptionalStringListDeserializer;

import java.util.List;
import java.util.Optional;

public record UpdateArticleRequest(ArticleUpdateDto article) {
    public record ArticleUpdateDto(
        String title,
        String description,
        String body,
        @JsonDeserialize(using = OptionalStringListDeserializer.class)
        Optional<List<String>> tagList
    ) {}
}
