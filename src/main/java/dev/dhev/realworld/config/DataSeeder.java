package dev.dhev.realworld.config;

import dev.dhev.realworld.article.Article;
import dev.dhev.realworld.article.ArticleRepository;
import dev.dhev.realworld.article.Tag;
import dev.dhev.realworld.article.TagRepository;
import dev.dhev.realworld.comment.Comment;
import dev.dhev.realworld.comment.CommentRepository;
import dev.dhev.realworld.profile.Follow;
import dev.dhev.realworld.profile.FollowRepository;
import dev.dhev.realworld.user.User;
import dev.dhev.realworld.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) {
            log.info("Seed data already present, skipping");
            return;
        }

        var jake = user("jake", "jake@jake.jake", "jakejake", "I work at statefarm.");
        var john = user("john", "john@john.john", "johnjohn", "I work at john.com");
        var jane = user("jane", "jane@jane.jane", "janejane", null);
        userRepository.saveAll(List.of(jake, john, jane));

        var dragonArticle  = article(jake, "How to Train Your Dragon",
                "Ever wondered how?",
                "It takes patience and trust. First, establish eye contact...",
                List.of("dragons", "training"));
        var dragonArticle2 = article(jake, "How to Train Your Dragon 2",
                "So you've trained one dragon. Now what?",
                "The second dragon is always harder. Once you go two dragons you never go back...",
                List.of("dragons", "training", "sequel"));
        var pragmaticArticle = article(jake, "The Pragmatic Coder",
                "On the philosophy of writing clean code",
                "Clean code is not about perfection. It is about honesty with your future self...",
                List.of("coding", "philosophy"));
        var iceAndFire = article(john, "Song of Ice and Fire",
                "A tale of dragons and kingdoms",
                "Winter is coming, and so are the dragons. The north remembers...",
                List.of("dragons", "fantasy"));
        var tlsArticle  = article(john, "TLS in a Nutshell",
                "How TLS actually works under the hood",
                "Every HTTPS connection starts with a handshake. Let's break it down step by step...",
                List.of("security", "networking", "coding"));
        articleRepository.saveAll(List.of(dragonArticle, dragonArticle2, pragmaticArticle, iceAndFire, tlsArticle));

        followRepository.saveAll(List.of(
            new Follow(jake, john),
            new Follow(john, jake),
            new Follow(jane, jake),
            new Follow(jane, john)
        ));

        articleRepository.addFavorite(dragonArticle.getId(),  jane.getId());
        articleRepository.addFavorite(dragonArticle.getId(),  john.getId());
        articleRepository.addFavorite(dragonArticle2.getId(), jane.getId());
        articleRepository.addFavorite(iceAndFire.getId(),     jake.getId());
        articleRepository.addFavorite(tlsArticle.getId(),     jake.getId());

        var comments = List.of(
            comment(john, dragonArticle,  "Great intro, Jake! The eye contact tip changed everything for me."),
            comment(jane, dragonArticle,  "I tried this and my dragon is now my best friend."),
            comment(jake, iceAndFire,     "The north remembers — and so does the reader. Loved it."),
            comment(jane, iceAndFire,     "When is the next chapter dropping, John?"),
            comment(jake, tlsArticle,     "Best TLS breakdown I have read. Bookmarked."),
            comment(john, pragmaticArticle, "This resonates deeply. Sending it to my whole team.")
        );
        commentRepository.saveAll(comments);

        log.info("Seed data loaded: 3 users, 5 articles, 4 follows, 5 favorites, 6 comments");
    }

    private User user(String username, String email, String password, String bio) {
        var u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(password));
        u.setBio(bio);
        return u;
    }

    private Article article(User author, String title, String description, String body, List<String> tagNames) {
        var a = new Article();
        a.setAuthor(author);
        a.setTitle(title);
        a.setSlug(title.toLowerCase().replaceAll("[^a-z0-9\\s]", "").trim().replaceAll("\\s+", "-"));
        a.setDescription(description);
        a.setBody(body);
        tagNames.forEach(name -> {
            var tag = tagRepository.findByName(name).orElseGet(() -> tagRepository.save(new Tag(name)));
            a.getTags().add(tag);
        });
        return a;
    }

    private Comment comment(User author, Article article, String body) {
        var c = new Comment();
        c.setAuthor(author);
        c.setArticle(article);
        c.setBody(body);
        return c;
    }
}
