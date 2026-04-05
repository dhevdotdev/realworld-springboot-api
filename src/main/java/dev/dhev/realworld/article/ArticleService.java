package dev.dhev.realworld.article;

import dev.dhev.realworld.article.dto.*;
import dev.dhev.realworld.profile.FollowId;
import dev.dhev.realworld.profile.FollowRepository;
import dev.dhev.realworld.shared.OffsetPageRequest;
import dev.dhev.realworld.shared.exception.ForbiddenException;
import dev.dhev.realworld.shared.exception.ResourceNotFoundException;
import dev.dhev.realworld.shared.exception.ValidationException;
import dev.dhev.realworld.user.User;
import dev.dhev.realworld.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private static final Logger log = LoggerFactory.getLogger(ArticleService.class);

    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional
    public ArticleResponse createArticle(CreateArticleRequest request, String currentUserEmail) {
        var dto = request.article();
        if (isBlank(dto.title())) throw new ValidationException("title", "can't be blank");
        if (isBlank(dto.description())) throw new ValidationException("description", "can't be blank");
        if (isBlank(dto.body())) throw new ValidationException("body", "can't be blank");

        var author = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("user"));

        var article = new Article();
        article.setSlug(generateSlug(dto.title()));
        article.setTitle(dto.title());
        article.setDescription(dto.description());
        article.setBody(dto.body());
        article.setAuthor(author);
        if (dto.tagList() != null) {
            article.getTags().addAll(resolveTags(dto.tagList()));
        }
        articleRepository.save(article);
        log.info("Article created: {}", article.getSlug());
        return ArticleResponse.from(article, false, false, 0);
    }

    @Transactional(readOnly = true)
    public ArticleListResponse listArticles(String tag, String author, String favorited,
                                            int limit, long offset, String currentUserEmail) {
        var spec = buildSpec(tag, author, favorited);
        var pageable = new OffsetPageRequest(offset, limit);
        var page = articleRepository.findAll(spec, pageable);
        var viewer = resolveViewer(currentUserEmail);
        var items = page.getContent().stream()
                .map(a -> toListItem(a, viewer))
                .toList();
        return new ArticleListResponse(items, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ArticleListResponse getFeed(String currentUserEmail, int limit, long offset) {
        var user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("user"));
        var followingIds = followRepository.findFollowingIdsByFollowerId(user.getId());
        if (followingIds.isEmpty()) {
            return new ArticleListResponse(List.of(), 0);
        }
        var pageable = new OffsetPageRequest(offset, limit);
        var page = articleRepository.findByAuthorIdIn(followingIds, pageable);
        var items = page.getContent().stream()
                .map(a -> toListItem(a, user))
                .toList();
        return new ArticleListResponse(items, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ArticleResponse getArticle(String slug, String currentUserEmail) {
        var article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("article"));
        var viewer = resolveViewer(currentUserEmail);
        return ArticleResponse.from(article,
                isFavorited(article, viewer),
                isFollowing(article.getAuthor(), viewer),
                articleRepository.countFavoritedBy(article.getId()));
    }

    @Transactional
    public ArticleResponse updateArticle(String slug, UpdateArticleRequest request, String currentUserEmail) {
        var article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("article"));
        var user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("user"));
        if (!article.getAuthor().getId().equals(user.getId()))
            throw new ForbiddenException("article");

        var dto = request.article();
        if (dto.title() != null) {
            if (isBlank(dto.title())) throw new ValidationException("title", "can't be blank");
            article.setTitle(dto.title());
        }
        if (dto.description() != null) {
            if (isBlank(dto.description())) throw new ValidationException("description", "can't be blank");
            article.setDescription(dto.description());
        }
        if (dto.body() != null) {
            if (isBlank(dto.body())) throw new ValidationException("body", "can't be blank");
            article.setBody(dto.body());
        }

        @SuppressWarnings("OptionalAssignedToNull")
        var tagList = dto.tagList();
        if (tagList != null) {
            if (tagList.isEmpty()) throw new ValidationException("tagList", "can't be null");
            article.getTags().clear();
            article.getTags().addAll(resolveTags(tagList.get()));
        }
        articleRepository.save(article);
        return ArticleResponse.from(article,
                isFavorited(article, user),
                isFollowing(article.getAuthor(), user),
                articleRepository.countFavoritedBy(article.getId()));
    }

    @Transactional
    public void deleteArticle(String slug, String currentUserEmail) {
        var article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("article"));
        var user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("user"));
        if (!article.getAuthor().getId().equals(user.getId()))
            throw new ForbiddenException("article");
        articleRepository.delete(article);
        log.info("Article deleted: {}", slug);
    }

    @Transactional
    public ArticleResponse favorite(String slug, String currentUserEmail) {
        var article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("article"));
        var user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("user"));
        articleRepository.addFavorite(article.getId(), user.getId());
        long count = articleRepository.countFavoritedBy(article.getId());
        return ArticleResponse.from(article, true, isFollowing(article.getAuthor(), user), count);
    }

    @Transactional
    public ArticleResponse unfavorite(String slug, String currentUserEmail) {
        var article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("article"));
        var user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("user"));
        articleRepository.removeFavorite(article.getId(), user.getId());
        long count = articleRepository.countFavoritedBy(article.getId());
        return ArticleResponse.from(article, false, isFollowing(article.getAuthor(), user), count);
    }

    @Transactional(readOnly = true)
    public List<String> getAllTags() {
        return tagRepository.findAll().stream().map(Tag::getName).toList();
    }

    private Specification<Article> buildSpec(String tag, String author, String favorited) {
        Specification<Article> spec = null;
        if (tag != null)       spec = add(spec, ArticleSpecs.hasTag(tag));
        if (author != null)    spec = add(spec, ArticleSpecs.hasAuthor(author));
        if (favorited != null) spec = add(spec, ArticleSpecs.hasFavoritedBy(favorited));
        return spec;
    }

    private Specification<Article> add(Specification<Article> base, Specification<Article> next) {
        return base == null ? next : base.and(next);
    }

    private User resolveViewer(String email) {
        if (email == null) return null;
        return userRepository.findByEmail(email).orElse(null);
    }

    private ArticleListResponse.ArticleListItemDto toListItem(Article article, User viewer) {
        boolean favorited = isFavorited(article, viewer);
        boolean following = isFollowing(article.getAuthor(), viewer);
        long count = articleRepository.countFavoritedBy(article.getId());
        return ArticleListResponse.toListItem(article, favorited, following, count);
    }

    private boolean isFavorited(Article article, User viewer) {
        if (viewer == null) return false;
        return articleRepository.isFavoritedBy(article.getId(), viewer.getEmail());
    }

    private boolean isFollowing(User author, User viewer) {
        if (viewer == null) return false;
        return followRepository.existsById(new FollowId(viewer.getId(), author.getId()));
    }

    private List<Tag> resolveTags(List<String> tagNames) {
        return tagNames.stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(new Tag(name))))
                .toList();
    }

    private String generateSlug(String title) {
        String base = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .trim()
                .replaceAll("\\s+", "-");
        if (base.isEmpty()) base = "article";
        if (!articleRepository.existsBySlug(base)) return base;
        return base + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
