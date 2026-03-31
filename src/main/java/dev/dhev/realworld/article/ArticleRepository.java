package dev.dhev.realworld.article;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {
    Optional<Article> findBySlug(String slug);
    boolean existsBySlug(String slug);
    Page<Article> findByAuthorIdIn(Collection<Long> authorIds, Pageable pageable);
    long countByAuthorIdIn(Collection<Long> authorIds);

    @Query("SELECT COUNT(u) > 0 FROM Article a JOIN a.favoritedBy u WHERE a.id = :articleId AND u.email = :email")
    boolean isFavoritedBy(@Param("articleId") Long articleId, @Param("email") String email);
}
