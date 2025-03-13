package ru.practicum.stats.analyzer.service;

import ru.practicum.grpc.stats.recommendation.RecommendedEventProto;

import java.util.List;
import java.util.stream.Stream;

public interface RecommendationsService {
    Stream<RecommendedEventProto> getRecommendationsForUser(Long userId, Integer maxResults);

    Stream<RecommendedEventProto> getSimilarEvents(Long eventId, Long userId, Integer maxResults);

    Stream<RecommendedEventProto> getInteractionsCount(List<Long> eventIds);
}