package ai.pepperorg.happynews.controller;

import ai.pepperorg.happynews.model.Article;
import ai.pepperorg.happynews.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final NewsService newsService;
    private final Scheduler scheduler;

    @GetMapping("/search")
    public ResponseEntity<List<Article>> searchArticles(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(newsService.searchArticles(keyword, null, null, null, null, pageSize, page));
    }

    @PostMapping("/trigger-job")
    public ResponseEntity<String> triggerNewsJob() {
        try {
            JobDetail jobDetail = JobBuilder.newJob(ai.pepperorg.happynews.scheduler.NewsJob.class)
                    .withIdentity("manualTriggerJob")
                    .storeDurably()
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .forJob(jobDetail)
                    .startNow()
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            return ResponseEntity.ok("Job triggered successfully.");
        } catch (SchedulerException e) {
            return ResponseEntity.status(500).body("Error triggering job: " + e.getMessage());
        }
    }

    @PostMapping("/update-quartz")
    public ResponseEntity<String> updateQuartzJob(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String searchIn,
            @RequestParam(required = false) String sources,
            @RequestParam(required = false) String domains,
            @RequestParam(required = false) String excludeDomains,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer maxPages) {
        try {
            JobKey jobKey = new JobKey("newsJob");
            if (!scheduler.checkExists(jobKey)) {
                return ResponseEntity.badRequest().body("Job not found.");
            }

            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            JobDataMap jobDataMap = jobDetail.getJobDataMap();

            if (query != null) jobDataMap.put("query", query);
            if (searchIn != null) jobDataMap.put("searchIn", searchIn);
            if (sources != null) jobDataMap.put("sources", sources);
            if (domains != null) jobDataMap.put("domains", domains);
            if (excludeDomains != null) jobDataMap.put("excludeDomains", excludeDomains);
            if (language != null) jobDataMap.put("language", language);
            if (sortBy != null) jobDataMap.put("sortBy", sortBy);
            if (pageSize != null) jobDataMap.put("pageSize", pageSize);
            if (maxPages != null) jobDataMap.put("maxPages", maxPages);

            scheduler.addJob(jobDetail, true);

            return ResponseEntity.ok("Quartz job updated successfully.");
        } catch (SchedulerException e) {
            return ResponseEntity.status(500).body("Error updating Quartz job: " + e.getMessage());
        }
    }
}
