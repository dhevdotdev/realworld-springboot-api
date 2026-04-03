package dev.dhev.realworld.comment;

import dev.dhev.realworld.article.Article;
import dev.dhev.realworld.article.ArticleRepository;
import dev.dhev.realworld.comment.dto.CommentListResponse;
import dev.dhev.realworld.comment.dto.CommentResponse;
import dev.dhev.realworld.comment.dto.CreateCommentRequest;
import dev.dhev.realworld.profile.FollowId;
import dev.dhev.realworld.profile.FollowRepository;
import dev.dhev.realworld.shared.exception.ForbiddenException;
import dev.dhev.realworld.shared.exception.ResourceNotFoundException;
import dev.dhev.realworld.shared.exception.ValidationException;
import dev.dhev.realworld.user.User;
import dev.dhev.realworld.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional
    public CommentResponse addComment(String slug, CreateCommentRequest request, String currentUserEmail) {
        var body = request.comment().body();
        if (body == null || body.isBlank()) throw new ValidationException("body", "can't be blank");

        var article = findArticle(slug);
        var author = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("user"));

        var comment = new Comment();
        comment.setBody(body);
        comment.setArticle(article);
        comment.setAuthor(author);
        commentRepository.save(comment);
        log.info("Comment added: articleSlug={}, commentId={}", slug, comment.getId());
        return CommentResponse.from(comment, false);
    }

    @Transactional(readOnly = true)
    public CommentListResponse getComments(String slug, String currentUserEmail) {
        var article = findArticle(slug);
        var comments = commentRepository.findByArticleOrderByCreatedAtDesc(article);
        var viewer = resolveViewer(currentUserEmail);
        var flags = buildFollowingFlags(comments, viewer);
        return CommentListResponse.from(comments, flags);
    }

    @Transactional
    public void deleteComment(String slug, Long commentId, String currentUserEmail) {
        var article = findArticle(slug);
        var comment = commentRepository.findByIdAndArticle(commentId, article)
                .orElseThrow(() -> new ResourceNotFoundException("comment"));
        var user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("user"));
        if (!comment.getAuthor().getId().equals(user.getId()))
            throw new ForbiddenException("comment");
        commentRepository.delete(comment);
        log.info("Comment deleted: commentId={}, articleSlug={}", commentId, slug);
    }

    // --- private helpers ---

    private Article findArticle(String slug) {
        return articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("article"));
    }

    private User resolveViewer(String email) {
        if (email == null) return null;
        return userRepository.findByEmail(email).orElse(null);
    }

    private boolean[] buildFollowingFlags(List<Comment> comments, User viewer) {
        var flags = new boolean[comments.size()];
        if (viewer == null) return flags;
        for (int i = 0; i < comments.size(); i++) {
            var authorId = comments.get(i).getAuthor().getId();
            flags[i] = followRepository.existsById(new FollowId(viewer.getId(), authorId));
        }
        return flags;
    }
}
