package ai.pepperorg.happynews.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "fetch_history")
@Data
@NoArgsConstructor
public class FetchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int articlesFetched;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String query;

    private LocalDateTime fetchTime;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String details;
}
