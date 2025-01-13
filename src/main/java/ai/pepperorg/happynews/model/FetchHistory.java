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

    private int articlesFetched; // Count of articles fetched
    private String query;        // Search query used
    private LocalDateTime fetchTime; // Timestamp of the fetch

    @Column(columnDefinition = "TEXT")
    private String details; // Additional details or errors
}
