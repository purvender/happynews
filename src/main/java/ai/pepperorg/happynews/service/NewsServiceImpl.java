package ai.pepperorg.happynews.service;

import ai.pepperorg.happynews.model.Article;
import ai.pepperorg.happynews.model.NewsApiResponse;
import ai.pepperorg.happynews.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final ArticleRepository articleRepository;
    private final StorageService storageService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.newsapi.key}")
    private String newsApiKey;

    @Override
    public void fetchAndStoreArticles() {
        String url = "https://newsapi.org/v2/top-headlines?language=en&apiKey=" + newsApiKey;
        ResponseEntity<NewsApiResponse> response = restTemplate.getForEntity(url, NewsApiResponse.class);

        if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            for (NewsApiResponse.ArticleDTO dto : response.getBody().getArticles()) {
                Article article = mapToArticle(dto);
                if(dto.getUrlToImage() != null && !dto.getUrlToImage().isEmpty()) {
                    String path = storageService.storeImage(dto.getUrlToImage());
                    article.setLocalImagePath(path);
                }
                articleRepository.save(article);
            }
        } else {
            System.err.println("Failed to fetch articles: " + response.getStatusCode());
        }
    }

    private Article mapToArticle(NewsApiResponse.ArticleDTO dto) {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setDescription(dto.getDescription());
        article.setContent(dto.getContent());
        article.setUrl(dto.getUrl());
        if(dto.getSource() != null) {
            article.setSource(dto.getSource().getName());
        }
        if(dto.getPublishedAt() != null && !dto.getPublishedAt().isEmpty()) {
            try {
                LocalDateTime publishedAt = LocalDateTime.parse(dto.getPublishedAt(), DateTimeFormatter.ISO_DATE_TIME);
                article.setPublishedAt(publishedAt);
            } catch(Exception e) {
                article.setPublishedAt(LocalDateTime.now());
            }
        } else {
            article.setPublishedAt(LocalDateTime.now());
        }
        article.setImageUrl(dto.getUrlToImage());
        article.setLanguage("en");
        return article;
    }
}
