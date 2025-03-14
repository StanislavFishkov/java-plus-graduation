package ru.practicum.stats.analyzer.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stats.analyzer.model.UserAction;
import ru.practicum.stats.analyzer.model.UserActionId;
import ru.practicum.stats.analyzer.repository.projection.RecommendedEvent;

import java.util.List;
import java.util.stream.Stream;

public interface UserActionRepository extends JpaRepository<UserAction, UserActionId> {
    @Query("Select new ru.practicum.stats.analyzer.repository.projection.RecommendedEvent(ua.eventId as eventId, " +
            "   CAST(sum(ua.weight) AS float) as score) " +
            "from UserAction ua where ua.eventId in (:eventIds) " +
            "group by ua.eventId")
    Stream<RecommendedEvent> getInteractionsCount(@Param("eventIds") List<Long> eventIds);

    Stream<UserAction> findAllByUserId(Long userId, Pageable page);
}