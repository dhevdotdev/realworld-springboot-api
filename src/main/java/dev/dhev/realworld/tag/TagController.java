package dev.dhev.realworld.tag;

import dev.dhev.realworld.article.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final ArticleService articleService;

    @GetMapping
    public Map<String, List<String>> getTags() {
        return Map.of("tags", articleService.getAllTags());
    }
}
