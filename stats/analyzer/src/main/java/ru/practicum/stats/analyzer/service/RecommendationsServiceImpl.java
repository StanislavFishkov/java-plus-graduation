package ru.practicum.stats.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.grpc.stats.recommendation.RecommendedEventProto;
import ru.practicum.stats.analyzer.model.UserAction;
import ru.practicum.stats.analyzer.model.UserActionId;
import ru.practicum.stats.analyzer.repository.EventSimilarityRepository;
import ru.practicum.stats.analyzer.repository.UserActionRepository;
import ru.practicum.stats.analyzer.repository.projection.RecommendedEvent;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationsServiceImpl implements RecommendationsService {
    private final UserActionRepository userActionRepository;
    private final EventSimilarityRepository eventSimilarityRepository;

    private static final int LIMIT_N = 10;
    private static final int LIMIT_K = 10;

    @Override
    public Stream<RecommendedEventProto> getRecommendationsForUser(Long userId, Integer maxResults) {
        List<Long> recentEvents = userActionRepository.findAllByUserId(userId,
                PageRequest.of(0, LIMIT_N, Sort.by(Sort.Order.desc("timestamp"))))
                .map(UserAction::getEventId)
                .toList();

        if (recentEvents.isEmpty()) return Stream.empty();

        return recentEvents.stream()
                .flatMap(eventId -> eventSimilarityRepository.getSimilarEventsWithoutInteractions(eventId, userId, maxResults))
                .sorted(Comparator.comparingDouble(RecommendedEvent::score).reversed())
                .map(RecommendedEvent::eventId)
                .distinct()
                .limit(maxResults)
                .map(eventId -> getPredictedScore(eventId, userId))
                .toList().stream();
    }

    @Override
    public Stream<RecommendedEventProto> getSimilarEvents(Long eventId, Long userId, Integer maxResults) {
        return eventSimilarityRepository.getSimilarEventsWithoutInteractions(eventId, userId, maxResults)
                .map(se -> RecommendedEventProto.newBuilder()
                        .setEventId(se.eventId())
                        .setScore(se.score())
                        .build())
                .toList().stream();
    }

    @Override
    public Stream<RecommendedEventProto> getInteractionsCount(List<Long> eventIds) {
        Map<Long, Float> eventTotalWeights = userActionRepository.getInteractionsCount(eventIds)
                .collect(Collectors.toMap(RecommendedEvent::eventId, RecommendedEvent::score));

        return eventIds.stream()
                .map(eventId -> RecommendedEventProto.newBuilder()
                        .setEventId(eventId)
                        .setScore(eventTotalWeights.getOrDefault(eventId, 0f))
                        .build());
    }

    private RecommendedEventProto getPredictedScore(Long eventId, Long userId) {
        List<RecommendedEvent> similarEvents = eventSimilarityRepository
                .getSimilarEventsWithInteractions(eventId, userId, LIMIT_K).toList();

        Map<Long, Float> eventWeights = userActionRepository.findAllById(similarEvents.stream()
                .map(e -> new UserActionId(e.eventId(), userId))
                .toList()
        ).stream().collect(Collectors.toMap(UserAction::getEventId, UserAction::getWeight));

        double weighedScores = similarEvents.stream()
                .mapToDouble(e -> e.score() * eventWeights.getOrDefault(e.eventId(), 0f))
                .sum();

        double sumScores = similarEvents.stream()
                .mapToDouble(RecommendedEvent::score)
                .sum();

        return RecommendedEventProto.newBuilder()
                .setEventId(eventId)
                .setScore((float) (sumScores == 0 ? 0 : weighedScores / sumScores))
                .build();
    }
}