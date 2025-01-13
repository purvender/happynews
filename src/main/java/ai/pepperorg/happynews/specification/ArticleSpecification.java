package ai.pepperorg.happynews.specification;

import ai.pepperorg.happynews.model.Article;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ArticleSpecification {

    public static Specification<Article> hasKeyword(String keyword) {
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("description")), "%" + keyword.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("content")), "%" + keyword.toLowerCase() + "%")
        );
    }

    public static Specification<Article> hasSource(String source) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("source")), source.toLowerCase());
    }

    public static Specification<Article> hasLanguage(String language) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("language")), language.toLowerCase());
    }

    public static Specification<Article> publishedAfter(LocalDate date) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("publishedAt"), date.atStartOfDay());
    }
}
