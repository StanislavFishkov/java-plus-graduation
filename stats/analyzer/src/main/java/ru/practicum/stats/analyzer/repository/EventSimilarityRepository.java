package ru.practicum.stats.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.stats.analyzer.model.EventSimilarity;
import ru.practicum.stats.analyzer.model.EventSimilarityId;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, EventSimilarityId> {
}