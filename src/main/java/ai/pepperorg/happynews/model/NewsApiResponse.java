package ai.pepperorg.happynews.model;

import lombok.Data;

import java.util.List;

@Data
public class NewsApiResponse {
    private String status;
    private int totalResults;
    private List<ArticleDTO> articles;

    @Data
    public static class ArticleDTO {
        private SourceDTO source;
        private String author;
        private String title;
        private String description;
        private String url;
        private String urlToImage;
        private String publishedAt;
        private String content;

        @Data
        public static class SourceDTO {
            private String id;
            private String name;
        }
    }
}
