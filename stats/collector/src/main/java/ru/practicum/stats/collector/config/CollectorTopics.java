package ru.practicum.stats.collector.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "collector.topic")
public record CollectorTopics(String userActionsTopic) {
}