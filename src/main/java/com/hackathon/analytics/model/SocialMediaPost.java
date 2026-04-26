package com.hackathon.analytics.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class SocialMediaPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String platform;
    private String author;

    @Column(length = 1000)
    private String content;

    private double sentimentScore; // -1.0 (Negative) to 1.0 (Positive)
    private String keyword;        // The brand or topic being tracked
    private LocalDateTime timestamp;

    // Default constructor required by JPA
    public SocialMediaPost() {}

    public SocialMediaPost(String platform, String author, String content, double sentimentScore, String keyword) {
        this.platform = platform;
        this.author = author;
        this.content = content;
        this.sentimentScore = sentimentScore;
        this.keyword = keyword;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public String getPlatform() { return platform; }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public double getSentimentScore() { return sentimentScore; }
    public String getKeyword() { return keyword; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // Setters (if needed for updates, though typically analytics data is immutable after ingestion)
    public void setPlatform(String platform) { this.platform = platform; }
    public void setAuthor(String author) { this.author = author; }
    public void setContent(String content) { this.content = content; }
    public void setSentimentScore(double sentimentScore) { this.sentimentScore = sentimentScore; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}