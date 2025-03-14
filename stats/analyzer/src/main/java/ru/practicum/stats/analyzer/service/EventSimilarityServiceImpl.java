package ru.practicum.stats.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.stats.analyzer.model.EventSimilarity;
import ru.practicum.stats.analyzer.repository.EventSimilarityRepository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventSimilarityServiceImpl implements EventSimilarityService {
    private final EventSimilarityRepository eventSimilarityRepository;
    private final Map<Long, Map<Long, Instant>> lastHandledTimestamps = new HashMap<>();

    @Override
    @Transactional
    public void handleEventSimilarity(EventSimilarityAvro eventSimilarityAvro) {
        var eventA = eventSimilarityAvro.getEventA();
        var eventB = eventSimilarityAvro.getEventB();
        if (lastHandledTimestamps.containsKey(eventA)
                && lastHandledTimestamps.get(eventA).containsKey(eventB)
                && lastHandledTimestamps.get(eventA).get(eventB).isAfter(eventSimilarityAvro.getTimestamp())) {
            log.info("Events similarity timestamp is before last handled for the pair and thus will not be handled: {}",
                    eventSimilarityAvro);
            return;
        }

        EventSimilarity eventSimilarity = eventSimilarityRepository.save(EventSimilarity.builder()
                .eventA(eventA)
                .eventB(eventB)
                .score(eventSimilarityAvro.getScore())
                .build()
        );

        log.info("Event similarity has been saved: {}", eventSimilarity);
        lastHandledTimestamps
                .computeIfAbsent(eventA, e -> new HashMap<>())
                .put(eventB, eventSimilarityAvro.getTimestamp());
    }
}