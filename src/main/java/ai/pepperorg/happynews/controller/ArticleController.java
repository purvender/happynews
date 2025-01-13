package ai.pepperorg.happynews.controller;

import ai.pepperorg.happynews.model.Article;
import ai.pepperorg.happynews.repository.ArticleRepository;
import ai.pepperorg.happynews.service.NewsService;
import ai.pepperorg.happynews.specification.ArticleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleRepository repository;
    private final NewsService newsService;  // Injected NewsService

    @GetMapping("/search")
    public ResponseEntity<List<Article>> searchArticles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate publishedAfter
    ) {
        Specification<Article> spec = Specification.where(null);
        if(keyword != null && !keyword.isEmpty()) {
            spec = spec.and(ArticleSpecification.hasKeyword(keyword));
        }
        if(source != null && !source.isEmpty()) {
            spec = spec.and(ArticleSpecification.hasSource(source));
        }
        if(language != null && !language.isEmpty()) {
            spec = spec.and(ArticleSpecification.hasLanguage(language));
        }
        if(publishedAfter != null) {
            spec = spec.and(ArticleSpecification.publishedAfter(publishedAfter));
        }

        List<Article> results = repository.findAll(spec);
        return ResponseEntity.ok(results);
    }

    // Temporary endpoint to manually trigger news fetching
    @PostMapping("/fetch")
    public ResponseEntity<String> fetchNewsManually() {
        newsService.fetchAndStoreArticles();
        return ResponseEntity.ok("News fetched and stored");
    }
}
