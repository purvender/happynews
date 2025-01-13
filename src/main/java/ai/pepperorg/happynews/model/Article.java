package ai.pepperorg.happynews.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
@Data
@NoArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2048)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String url;
    private String source;

    private LocalDateTime publishedAt;
    private String imageUrl;
    private String localImagePath;
    private String language;

    private LocalDateTime createdAt = LocalDateTime.now();
}
