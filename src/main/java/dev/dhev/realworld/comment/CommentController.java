package dev.dhev.realworld.comment;

import dev.dhev.realworld.comment.dto.CommentListResponse;
import dev.dhev.realworld.comment.dto.CommentResponse;
import dev.dhev.realworld.comment.dto.CreateCommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles/{slug}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public CommentResponse addComment(@PathVariable String slug,
                                      @RequestBody CreateCommentRequest request,
                                      Authentication authentication) {
        return commentService.addComment(slug, request, authentication.getName());
    }

    @GetMapping
    public CommentListResponse getComments(@PathVariable String slug, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return commentService.getComments(slug, email);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void deleteComment(@PathVariable String slug,
                              @PathVariable Long id,
                              Authentication authentication) {
        commentService.deleteComment(slug, id, authentication.getName());
    }
}
