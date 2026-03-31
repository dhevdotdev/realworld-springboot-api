package dev.dhev.realworld.article;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

class ArticleSpecs {

    private ArticleSpecs() {}

    static Specification<Article> hasTag(String tagName) {
        return (root, query, cb) -> {
            var tags = root.join("tags", JoinType.INNER);
            return cb.equal(tags.get("name"), tagName);
        };
    }

    static Specification<Article> hasAuthor(String username) {
        return (root, query, cb) -> {
            var author = root.join("author", JoinType.INNER);
            return cb.equal(author.get("username"), username);
        };
    }

    static Specification<Article> hasFavoritedBy(String username) {
        return (root, query, cb) -> {
            var favoritedBy = root.join("favoritedBy", JoinType.INNER);
            return cb.equal(favoritedBy.get("username"), username);
        };
    }
}
