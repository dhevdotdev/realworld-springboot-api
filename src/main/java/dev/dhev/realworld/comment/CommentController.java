package dev.dhev.realworld.comment;

import dev.dhev.realworld.comment.dto.CommentListResponse;
import dev.dhev.realworld.comment.dto.CommentResponse;
import dev.dhev.realworld.comment.dto.CreateCommentRequest;
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
@RequestMapping("/api/articles/{slug}/comments")
@RequiredArgsConstructor
@Tag(name = "Comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Add a comment",
               description = "Adds a comment to the given article.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Comment added"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
        @ApiResponse(responseCode = "404", description = "Article not found"),
        @ApiResponse(responseCode = "422", description = "Validation failed — body is blank")
    })
    public CommentResponse addComment(
            @Parameter(description = "Slug of the article to comment on") @PathVariable String slug,
            @RequestBody CreateCommentRequest request,
            Authentication authentication) {
        return commentService.addComment(slug, request, authentication.getName());
    }

    @GetMapping
    @Operation(summary = "Get comments",
               description = "Returns all comments for the given article, ordered newest first. Auth is optional — `following` is always `false` for unauthenticated requests.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comments returned"),
        @ApiResponse(responseCode = "404", description = "Article not found")
    })
    public CommentListResponse getComments(
            @Parameter(description = "Slug of the article") @PathVariable String slug,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return commentService.getComments(slug, email);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a comment",
               description = "Deletes the given comment. Only the comment's author may delete it.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Comment deleted"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
        @ApiResponse(responseCode = "403", description = "Not the comment's author"),
        @ApiResponse(responseCode = "404", description = "Article or comment not found")
    })
    public void deleteComment(
            @Parameter(description = "Slug of the article") @PathVariable String slug,
            @Parameter(description = "ID of the comment to delete") @PathVariable Long id,
            Authentication authentication) {
        commentService.deleteComment(slug, id, authentication.getName());
    }
}
