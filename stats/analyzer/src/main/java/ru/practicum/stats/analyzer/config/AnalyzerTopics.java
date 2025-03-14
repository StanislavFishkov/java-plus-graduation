package ru.practicum.stats.analyzer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "analyzer.topic")
public record AnalyzerTopics(String userActionsTopic, String eventsSimilarityTopic) {
}