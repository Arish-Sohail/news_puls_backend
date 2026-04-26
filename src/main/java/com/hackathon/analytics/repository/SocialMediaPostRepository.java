package com.hackathon.analytics.repository;

import com.hackathon.analytics.model.SocialMediaPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SocialMediaPostRepository extends JpaRepository<SocialMediaPost, Long> {

    // Find all mentions for a specific keyword
    List<SocialMediaPost> findByKeywordOrderByTimestampDesc(String keyword);

    // Count total mentions for a keyword
    long countByKeyword(String keyword);

    // Calculate average sentiment for a keyword using a custom SQL query
    @Query("SELECT AVG(s.sentimentScore) FROM SocialMediaPost s WHERE s.keyword = :keyword")
    Double getAverageSentimentByKeyword(@Param("keyword") String keyword);
}