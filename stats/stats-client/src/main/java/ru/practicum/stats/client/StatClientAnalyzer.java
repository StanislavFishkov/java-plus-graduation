package ru.practicum.stats.client;

import ru.practicum.grpc.stats.recommendation.RecommendedEventProto;

import java.util.List;
import java.util.stream.Stream;

public interface StatClientAnalyzer {
    Stream<RecommendedEventProto> getRecommendationsForUser(long userId, int maxResults);

    Stream<RecommendedEventProto> getSimilarEvents(long eventId, long userId, int maxResults);

    Stream<RecommendedEventProto> getInteractionsCount(List<Long> eventIds);
}