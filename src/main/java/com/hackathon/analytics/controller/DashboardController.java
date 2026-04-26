package com.hackathon.analytics.controller;

import com.hackathon.analytics.model.SocialMediaPost;
import com.hackathon.analytics.service.AnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/") // We changed this to the root so we can catch the home page
@CrossOrigin(origins = "*")
public class DashboardController {

    private final AnalyticsService analyticsService;

    public DashboardController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // NEW: A friendly home page message so you don't get an error!
    @GetMapping
    public String home() {
        return "<h1>🚀 Server is Running!</h1>" +
                "<p>Go to <a href='/api/analytics/feed'>/api/analytics/feed</a> to see live data.</p>";
    }

    // GET: /api/analytics/feed
    @GetMapping("api/analytics/feed")
    public List<SocialMediaPost> getLiveFeed() {
        return analyticsService.getLiveFeed();
    }

    // NEW API: Trigger an instant data fetch from NewsAPI when the user searches
    @GetMapping("api/analytics/fetch-now")
    public String triggerFetch(@RequestParam String keyword) {
        analyticsService.fetchNewsDataForKeyword(keyword);
        return "Fetch triggered successfully for " + keyword;
    }

    // GET: /api/analytics/summary?keyword=Java
    @GetMapping("api/analytics/summary")
    public Map<String, Object> getKeywordSummary(@RequestParam String keyword) {
        return analyticsService.getKeywordSummary(keyword);
    }
}