package ai.pepperorg.happynews.service;

import ai.pepperorg.happynews.model.Article;
import ai.pepperorg.happynews.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractNewsService implements NewsService {

    protected final ArticleRepository articleRepository;

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
                .filter(article -> keyword == null
                        || article.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
}
