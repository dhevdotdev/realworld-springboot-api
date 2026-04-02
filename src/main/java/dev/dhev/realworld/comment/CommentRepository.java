package dev.dhev.realworld.comment;

import dev.dhev.realworld.article.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticleOrderByCreatedAtDesc(Article article);
    Optional<Comment> findByIdAndArticle(Long id, Article article);
}
