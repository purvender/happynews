package ai.pepperorg.happynews.service;

import ai.pepperorg.happynews.model.Article;
import ai.pepperorg.happynews.model.NewsApiResponse;
import ai.pepperorg.happynews.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsServiceImpl implements NewsService {

    private final ArticleRepository articleRepository;
    private final StorageService storageService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.newsapi.key}")
    private String newsApiKey;

    @Override
    public void fetchAndStoreArticles(String query, String searchIn, String sources, String domains,
                                      String excludeDomains, String language, String sortBy,
                                      int pageSize, int maxPages) {
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
                    articles.forEach(dto -> {
                        Article article = mapToArticle(dto);
                        if (dto.getUrlToImage() != null) {
                            String localPath = storageService.storeImage(dto.getUrlToImage());
                            article.setLocalImagePath(localPath);
                        }
                        articleRepository.save(article);
                    });
                } else {
                    log.error("Error fetching articles: {}", response.getStatusCode());
                }
            } catch (Exception e) {
                log.error("Error fetching articles: {}", e.getMessage());
            }
        }
    }

    @Override
    public List<Article> getLatestArticles(int pageSize) {
        return articleRepository.findAll(Sort.by(Sort.Direction.DESC, "publishedAt"))
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> searchArticles(String keyword, String source, String domain, String language,
                                        String sortBy, int pageSize, int page) {
        return articleRepository.findAll().stream()
                .filter(article -> keyword == null || article.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
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
