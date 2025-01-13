package ai.pepperorg.happynews.repository;

import ai.pepperorg.happynews.model.FetchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FetchHistoryRepository extends JpaRepository<FetchHistory, Long> {
}
