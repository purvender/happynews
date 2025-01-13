package ai.pepperorg.happynews.service;

import ai.pepperorg.happynews.model.NewsApiResponse.ArticleDTO;
import java.util.List;

public interface ExternalNewsSource {
    List<ArticleDTO> fetchArticles();
}
