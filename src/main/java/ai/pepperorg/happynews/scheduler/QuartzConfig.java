package ai.pepperorg.happynews.scheduler;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail newsJobDetail() {
        return JobBuilder.newJob(NewsJob.class)
                .withIdentity("newsJob")
                // Adding powerful, motivational, and positive search keywords
                .usingJobData("query", String.join(" OR ",
                        "latest",
                        "motivational",
                        "spiritual",
                        "god",
                        "good news",
                        "health",
                        "mental health",
                        "happiness",
                        "success",
                        "mindfulness",
                        "positivity",
                        "well-being",
                        "self-improvement",
                        "personal growth",
                        "inspiration",
                        "optimism",
                        "resilience",
                        "kindness",
                        "joy",
                        "achievement",
                        "india",
                        "hindu",
                        "krishna",
                        "ram",
                        "gratitude"))
                .usingJobData("searchIn", "title,description,content") // Search in title, description, and content
                .usingJobData("sources", "") // No specific sources by default
                .usingJobData("domains", "") // No specific domains by default
                .usingJobData("excludeDomains", "") // No excluded domains by default
                .usingJobData("language", "en") // Default to English
                .usingJobData("sortBy", "publishedAt") // Sort by publication date
                .usingJobData("pageSize", 10) // Default page size
                .usingJobData("maxPages", 1) // Default max pages
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger newsJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(newsJobDetail())
                .withIdentity("newsJobTrigger")
                .withSchedule(SimpleScheduleBuilder
                        .simpleSchedule()
                        .withIntervalInHours(6) // Run every 6 hours
                        .repeatForever())
                .build();
    }
}
