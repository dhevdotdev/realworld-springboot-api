package dev.dhev.realworld.article.dto;

import java.util.List;

public record CreateArticleRequest(ArticleCreateDto article) {
    public record ArticleCreateDto(String title, String description, String body, List<String> tagList) {}
}
