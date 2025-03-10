package ru.practicum.stats.aggregator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aggregator.topic")
public record AggregatorTopics(String userActionsTopic, String eventsSimilarityTopic) {
}