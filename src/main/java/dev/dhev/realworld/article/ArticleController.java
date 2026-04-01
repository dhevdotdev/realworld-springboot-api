package dev.dhev.realworld.article;

import dev.dhev.realworld.article.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public ArticleResponse createArticle(@RequestBody CreateArticleRequest request, Authentication authentication) {
        return articleService.createArticle(request, authentication.getName());
    }

    @GetMapping
    public ArticleListResponse listArticles(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String favorited,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") long offset,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return articleService.listArticles(tag, author, favorited, limit, offset, email);
    }

    @GetMapping("/feed")
    @PreAuthorize("isAuthenticated()")
    public ArticleListResponse getFeed(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") long offset,
            Authentication authentication) {
        return articleService.getFeed(authentication.getName(), limit, offset);
    }

    @GetMapping("/{slug}")
    public ArticleResponse getArticle(@PathVariable String slug, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return articleService.getArticle(slug, email);
    }

    @PutMapping("/{slug}")
    @PreAuthorize("isAuthenticated()")
    public ArticleResponse updateArticle(@PathVariable String slug,
                                         @RequestBody UpdateArticleRequest request,
                                         Authentication authentication) {
        return articleService.updateArticle(slug, request, authentication.getName());
    }

    @DeleteMapping("/{slug}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void deleteArticle(@PathVariable String slug, Authentication authentication) {
        articleService.deleteArticle(slug, authentication.getName());
    }

    @PostMapping("/{slug}/favorite")
    @PreAuthorize("isAuthenticated()")
    public ArticleResponse favorite(@PathVariable String slug, Authentication authentication) {
        return articleService.favorite(slug, authentication.getName());
    }

    @DeleteMapping("/{slug}/favorite")
    @PreAuthorize("isAuthenticated()")
    public ArticleResponse unfavorite(@PathVariable String slug, Authentication authentication) {
        return articleService.unfavorite(slug, authentication.getName());
    }
}
