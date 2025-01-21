package ai.pepperorg.happynews.service;

import ai.pepperorg.happynews.model.Article;
import ai.pepperorg.happynews.model.FetchHistory;
import ai.pepperorg.happynews.model.NewsApiResponse;
import ai.pepperorg.happynews.repository.ArticleRepository;
import ai.pepperorg.happynews.repository.FetchHistoryRepository;
import ai.pepperorg.happynews.service.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class NewsApiImpl extends AbstractNewsService {

    private final StorageService storageService;
    private final FetchHistoryRepository fetchHistoryRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.newsapi.key}")
    private String newsApiKey;

    public NewsApiImpl(ArticleRepository articleRepository,
                       StorageService storageService,
                       FetchHistoryRepository fetchHistoryRepository) {
        super(articleRepository);
        this.storageService = storageService;
        this.fetchHistoryRepository = fetchHistoryRepository;
    }

    @Override
    public void fetchAndStoreArticles(String query, String searchIn, String sources, String domains,
                                      String excludeDomains, String language, String sortBy,
                                      int pageSize, int maxPages) {
        int totalFetched = 0;
        StringBuilder detailsBuilder = new StringBuilder();

        for (int page = 1; page <= maxPages; page++) {
            try {
                StringBuilder urlBuilder = new StringBuilder("https://newsapi.org/v2/everything?");
                urlBuilder.append("apiKey=").append(newsApiKey);

                if (query != null) urlBuilder.append("&q=").append(URLEncoder.encode(query, StandardCharsets.UTF_8));
                if (searchIn != null) urlBuilder.append("&searchIn=").append(searchIn);
                if (sources != null) urlBuilder.append("&sources=").append(sources);
                if (domains != null) urlBuilder.append("&domains=").append(domains);
                if (excludeDomains != null) urlBuilder.append("&excludeDomains=").append(excludeDomains);
                if (language != null) urlBuilder.append("&language=").append(language);
                if (sortBy != null) urlBuilder.append("&sortBy=").append(sortBy);
                urlBuilder.append("&pageSize=").append(pageSize).append("&page=").append(page);

                ResponseEntity<NewsApiResponse> response = restTemplate.getForEntity(urlBuilder.toString(), NewsApiResponse.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    List<NewsApiResponse.ArticleDTO> articles = response.getBody().getArticles();
                    totalFetched += articles.size();

                    for (NewsApiResponse.ArticleDTO dto : articles) {
                        Article article = mapToArticle(dto);
                        if (dto.getUrlToImage() != null) {
                            String localPath = storageService.storeImage(dto.getUrlToImage());
                            article.setLocalImagePath(localPath);
                        }
                        articleRepository.save(article);
                    }
                    detailsBuilder.append("Page ")
                            .append(page)
                            .append(": Successfully fetched ")
                            .append(articles.size())
                            .append(" articles.\n");
                } else {
                    detailsBuilder.append("Error on page ").append(page)
                            .append(": ").append(response.getStatusCode()).append("\n");
                    log.error("Error fetching articles: {}", response.getStatusCode());
                }
            } catch (Exception e) {
                detailsBuilder.append("Exception on page ").append(page)
                        .append(": ").append(e.getMessage()).append("\n");
                log.error("Error fetching articles: {}", e.getMessage());
            }
        }

        // Create and save a FetchHistory record after fetching
        FetchHistory history = new FetchHistory();
        history.setArticlesFetched(totalFetched);
        history.setQuery(query);
        history.setFetchTime(LocalDateTime.now());
        history.setDetails(detailsBuilder.toString());

        fetchHistoryRepository.save(history);
    }

    private Article mapToArticle(NewsApiResponse.ArticleDTO dto) {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setDescription(dto.getDescription());
        article.setContent(dto.getContent());
        article.setUrl(dto.getUrl());
        article.setPublishedAt(parsePublishedAt(dto.getPublishedAt()));
        article.setImageUrl(dto.getUrlToImage());
        article.setSource(dto.getSource().getName());
        return article;
    }

    private LocalDateTime parsePublishedAt(String publishedAt) {
        if (publishedAt == null || publishedAt.isEmpty()) {
            return LocalDateTime.now();
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            return LocalDateTime.parse(publishedAt, formatter);
        } catch (Exception e) {
            log.error("Error parsing publishedAt: {}", publishedAt, e);
            return LocalDateTime.now();
        }
    }
}
