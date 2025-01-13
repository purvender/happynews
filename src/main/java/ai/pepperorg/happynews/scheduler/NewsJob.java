package ai.pepperorg.happynews.scheduler;

import ai.pepperorg.happynews.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsJob implements Job {

    private final NewsService newsService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        newsService.fetchAndStoreArticles();
    }
}
