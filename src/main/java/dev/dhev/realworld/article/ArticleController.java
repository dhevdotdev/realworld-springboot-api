package dev.dhev.realworld.article;

import dev.dhev.realworld.article.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@Tag(name = "Articles")
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    @Operation(summary = "List articles",
               description = """
                   Returns a paginated list of articles. All filters are optional and combinable.
                   Auth is optional — when authenticated, `favorited` and `following` flags are populated.
                   """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Articles returned")
    })
    public ArticleListResponse listArticles(
            @Parameter(description = "Filter by tag") @RequestParam(required = false) String tag,
            @Parameter(description = "Filter by author username") @RequestParam(required = false) String author,
            @Parameter(description = "Filter by username who favorited") @RequestParam(required = false) String favorited,
            @Parameter(description = "Max number of articles to return (default 20)") @RequestParam(defaultValue = "20") int limit,
            @Parameter(description = "Number of articles to skip (default 0)") @RequestParam(defaultValue = "0") long offset,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return articleService.listArticles(tag, author, favorited, limit, offset, email);
    }

    @GetMapping("/feed")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get feed",
               description = "Returns articles written by users the authenticated user follows, ordered by most recent first.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Feed returned"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid token")
    })
    public ArticleListResponse getFeed(
            @Parameter(description = "Max number of articles to return (default 20)") @RequestParam(defaultValue = "20") int limit,
            @Parameter(description = "Number of articles to skip (default 0)") @RequestParam(defaultValue = "0") long offset,
            Authentication authentication) {
        return articleService.getFeed(authentication.getName(), limit, offset);
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get an article",
               description = "Returns a single article by slug. Auth is optional.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Article returned"),
        @ApiResponse(responseCode = "404", description = "Article not found")
    })
    public ArticleResponse getArticle(
            @Parameter(description = "Slug of the article") @PathVariable String slug,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return articleService.getArticle(slug, email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create an article",
               description = "Creates a new article. The slug is auto-generated from the title.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Article created"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
        @ApiResponse(responseCode = "422", description = "Validation failed — blank required fields")
    })
    public ArticleResponse createArticle(@RequestBody CreateArticleRequest request, Authentication authentication) {
        return articleService.createArticle(request, authentication.getName());
    }

    @PutMapping("/{slug}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update an article",
               description = "Updates title, description, or body. All fields are optional — only provided fields are updated. Only the article's author may update it.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Article updated"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
        @ApiResponse(responseCode = "403", description = "Not the article's author"),
        @ApiResponse(responseCode = "404", description = "Article not found"),
        @ApiResponse(responseCode = "422", description = "Validation failed — blank field provided")
    })
    public ArticleResponse updateArticle(
            @Parameter(description = "Slug of the article to update") @PathVariable String slug,
            @RequestBody UpdateArticleRequest request,
            Authentication authentication) {
        return articleService.updateArticle(slug, request, authentication.getName());
    }

    @DeleteMapping("/{slug}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete an article",
               description = "Permanently deletes the article and its comments. Only the article's author may delete it.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Article deleted"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
        @ApiResponse(responseCode = "403", description = "Not the article's author"),
        @ApiResponse(responseCode = "404", description = "Article not found")
    })
    public void deleteArticle(
            @Parameter(description = "Slug of the article to delete") @PathVariable String slug,
            Authentication authentication) {
        articleService.deleteArticle(slug, authentication.getName());
    }

    @PostMapping("/{slug}/favorite")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Favorite an article", tags = "Favorites",
               description = "Adds the article to the authenticated user's favorites. Idempotent.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Article favorited"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
        @ApiResponse(responseCode = "404", description = "Article not found")
    })
    public ArticleResponse favorite(
            @Parameter(description = "Slug of the article to favorite") @PathVariable String slug,
            Authentication authentication) {
        return articleService.favorite(slug, authentication.getName());
    }

    @DeleteMapping("/{slug}/favorite")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Unfavorite an article", tags = "Favorites",
               description = "Removes the article from the authenticated user's favorites. Idempotent.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Article unfavorited"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
        @ApiResponse(responseCode = "404", description = "Article not found")
    })
    public ArticleResponse unfavorite(
            @Parameter(description = "Slug of the article to unfavorite") @PathVariable String slug,
            Authentication authentication) {
        return articleService.unfavorite(slug, authentication.getName());
    }
}
