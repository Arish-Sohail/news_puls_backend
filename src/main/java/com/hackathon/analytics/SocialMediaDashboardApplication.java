package com.hackathon.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication // This tells Spring Boot this is the main file to start the app
@EnableScheduling      // This turns on the background timer for our mock data generator
public class SocialMediaDashboardApplication {

    public static void main(String[] args) {
        // This line actually starts up the web server on your computer!
        SpringApplication.run(SocialMediaDashboardApplication.class, args);
        
        // This prints to your terminal so you know it worked
        System.out.println("🚀 Vibe Coding Success: Analytics Backend is LIVE on http://localhost:8080");
    }
}