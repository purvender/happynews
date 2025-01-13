package ai.pepperorg.happynews.service;

import ai.pepperorg.happynews.model.Article;

import java.util.List;

public interface NewsService {
    void fetchAndStoreArticles(String query, String searchIn, String sources, String domains,
                               String excludeDomains, String language, String sortBy,
                               int pageSize, int maxPages);

    List<Article> searchArticles(String keyword, String source, String domain, String language,
                                 String sortBy, int pageSize, int page);

    Article getArticleById(Long id);
}
