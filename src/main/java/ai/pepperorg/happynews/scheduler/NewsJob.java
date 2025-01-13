package ai.pepperorg.happynews.scheduler;

import ai.pepperorg.happynews.service.NewsService;
import ch.qos.logback.core.net.SyslogOutputStream;
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
        String query = context.getMergedJobDataMap().getString("query");
        String searchIn = context.getMergedJobDataMap().getString("searchIn");
        String sources = context.getMergedJobDataMap().getString("sources");
        String domains = context.getMergedJobDataMap().getString("domains");
        String excludeDomains = context.getMergedJobDataMap().getString("excludeDomains");
        String language = context.getMergedJobDataMap().getString("language");
        String sortBy = context.getMergedJobDataMap().getString("sortBy");
        int pageSize = context.getMergedJobDataMap().getInt("pageSize");
        int maxPages = context.getMergedJobDataMap().getInt("maxPages");

        try {
            newsService.fetchAndStoreArticles(query, searchIn, sources, domains, excludeDomains, language, sortBy, pageSize, maxPages);
        } catch (Exception e) {
            throw new JobExecutionException("Error executing NewsJob with advanced search", e);
        }
    }
}
