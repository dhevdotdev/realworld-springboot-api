package dev.dhev.realworld.tag;

import dev.dhev.realworld.article.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Tag(name = "Tags")
public class TagController {

    private final ArticleService articleService;

    @GetMapping
    @Operation(summary = "Get all tags",
               description = "Returns a list of all tags that have been used across all articles.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tags returned")
    })
    public Map<String, List<String>> getTags() {
        return Map.of("tags", articleService.getAllTags());
    }
}
