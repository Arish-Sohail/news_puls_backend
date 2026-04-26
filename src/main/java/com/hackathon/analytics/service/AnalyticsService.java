package com.hackathon.analytics.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.analytics.model.SocialMediaPost;
import com.hackathon.analytics.repository.SocialMediaPostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AnalyticsService {

    // ==============================================================
    // 🔑 PASTE YOUR NEWSAPI.AI KEY HERE
    // ==============================================================
    private static final String NEWS_API_KEY = "0e1b6b8f-610e-481f-8988-e8cf603a1160";

    private final SocialMediaPostRepository repository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // NEW: Keeps track of articles we've already saved to prevent duplicates
    private final Set<String> seenArticles = new HashSet<>();

    public AnalyticsService(SocialMediaPostRepository repository) {
        this.repository = repository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public List<SocialMediaPost> getLiveFeed() {
        List<SocialMediaPost> all = repository.findAll();
        Collections.reverse(all);
        return all.stream().limit(50).toList();
    }

    public Map<String, Object> getKeywordSummary(String keyword) {
        long totalMentions = repository.countByKeyword(keyword);
        Double avgSentiment = repository.getAverageSentimentByKeyword(keyword);

        Map<String, Object> response = new HashMap<>();
        response.put("keyword", keyword);
        response.put("totalMentions", totalMentions);
        response.put("averageSentiment", avgSentiment != null ? Math.round(avgSentiment * 100.0) / 100.0 : 0.0);
        return response;
    }

    public double analyzeSentiment(String text) {
        if (text == null) return 0.0;
        String lowerText = text.toLowerCase();
        double score = 0.0;
        List<String> positiveWords = Arrays.asList("love", "great", "amazing", "awesome", "good", "fast", "best", "pro", "easy", "growth", "profit", "success");
        List<String> negativeWords = Arrays.asList("hate", "terrible", "bad", "slow", "worst", "broken", "error", "fail", "bug", "crash", "loss", "decline");
        for (String word : positiveWords) { if (lowerText.contains(word)) score += 0.5; }
        for (String word : negativeWords) { if (lowerText.contains(word)) score -= 0.5; }
        return Math.max(-1.0, Math.min(1.0, score));
    }

    // ==============================================================
    // ON-DEMAND NEWS INTELLIGENCE ENGINE
    // Removed @Scheduled! It now fetches instantly when requested.
    // ==============================================================
    public void fetchNewsDataForKeyword(String keyword) {
        try {
            // newsapi.ai endpoint format - Loads 15 articles instantly
            String apiUrl = "https://newsapi.ai/api/v1/article/getArticles?keyword=" + keyword +
                    "&lang=eng&articlesCount=15&apiKey=" + NEWS_API_KEY;

            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode articles = root.path("articles").path("results");

            for (JsonNode article : articles) {
                // Extract news details
                String title = article.path("title").asText();

                // Prevent duplicate saves
                if (seenArticles.contains(title)) continue;
                seenArticles.add(title);

                String body = article.path("body").asText();
                String sourceName = article.path("source").path("title").asText("Web News");

                String content = "📰 " + title + " - " + body;
                if (content.length() > 800) content = content.substring(0, 800) + "...";

                double sentiment = analyzeSentiment(content);

                SocialMediaPost post = new SocialMediaPost(sourceName, sourceName, content, sentiment, keyword);
                repository.save(post);
            }
            System.out.println("✅ Synced real news data for: " + keyword);

        } catch (Exception e) {
            System.out.println("⚠️ API failed. Generating mock data for: " + keyword);
            generateFailsafeNewsData(keyword);
        }
    }

    private void generateFailsafeNewsData(String keyword) {
        String[] sources = {"TechCrunch", "Wired", "Forbes", "Developer Blog", "Reuters"};
        Random rand = new Random();
        String source = sources[rand.nextInt(sources.length)];

        String[] headlines = {
                "📰 New updates to " + keyword + " show massive performance gains.",
                "📰 Critical security vulnerability found in " + keyword + " systems.",
                "📰 Tech giants heavily investing in " + keyword + " this quarter.",
                "📰 Developers report frustration with recent " + keyword + " changes."
        };
        String content = headlines[rand.nextInt(headlines.length)];

        // NEW: Prevent duplicates in our mock data as well
        if (seenArticles.contains(content)) {
            return;
        }
        seenArticles.add(content);

        SocialMediaPost post = new SocialMediaPost(source, source, content, analyzeSentiment(content), keyword);
        repository.save(post);
    }
}