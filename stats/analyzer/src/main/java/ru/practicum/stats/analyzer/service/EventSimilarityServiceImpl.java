package ru.practicum.stats.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventSimilarityServiceImpl implements EventSimilarityService {
    private final Map<Long, Map<Long, Instant>> lastHandledTimestamps = new HashMap<>();

    @Override
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


        log.info("Event similarity has been handled: {}", eventSimilarityAvro);
        lastHandledTimestamps
                .computeIfAbsent(eventA, e -> new HashMap<>())
                .put(eventB, eventSimilarityAvro.getTimestamp());
    }
}