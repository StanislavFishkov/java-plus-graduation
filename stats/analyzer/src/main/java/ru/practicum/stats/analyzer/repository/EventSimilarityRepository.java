package ru.practicum.stats.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stats.analyzer.model.EventSimilarity;
import ru.practicum.stats.analyzer.model.EventSimilarityId;
import ru.practicum.stats.analyzer.repository.projection.RecommendedEvent;

import java.util.stream.Stream;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, EventSimilarityId> {
    @Query("Select new ru.practicum.stats.analyzer.repository.projection.RecommendedEvent((CASE WHEN es.eventA = :eventId THEN es.eventB ELSE es.eventA END) as eventId, es.score as score) " +
            "from EventSimilarity es " +
            "where (es.eventA = :eventId or es.eventB = :eventId) " +
            "   and (CASE WHEN es.eventA = :eventId THEN es.eventB ELSE es.eventA END) " +
            "       NOT IN (Select ua.eventId from UserAction ua where ua.userId = :userId) " +
            "order by es.score DESC " +
            "limit :maxResults")
    Stream<RecommendedEvent> getSimilarEventsWithoutInteractions(@Param("eventId") Long eventId,
                                                                 @Param("userId") Long userId,
                                                                 @Param("maxResults") Integer maxResults);

    @Query("Select new ru.practicum.stats.analyzer.repository.projection.RecommendedEvent(" +
            "   (CASE WHEN es.eventA = :eventId THEN es.eventB ELSE es.eventA END) as eventId, es.score as score) " +
            "from EventSimilarity es " +
            "where (es.eventA = :eventId or es.eventB = :eventId) " +
            "   and (CASE WHEN es.eventA = :eventId THEN es.eventB ELSE es.eventA END) " +
            "       IN (Select ua.eventId from UserAction ua where ua.userId = :userId) " +
            "order by es.score DESC " +
            "limit :maxResults")
    Stream<RecommendedEvent> getSimilarEventsWithInteractions(@Param("eventId") Long eventId,
                                                                 @Param("userId") Long userId,
                                                                 @Param("maxResults") Integer maxResults);
}