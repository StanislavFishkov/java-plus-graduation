package ru.practicum.stats.aggregator.service;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserActionServiceImpl implements UserActionService {
    // raw data
    Map<Long, Map<Long, Double>> weightsByUsers = new HashMap<>(); // Map<Event, Map<User, Weight>>

    // precalculated data
    Map<Long, Double> weightsSums = new HashMap<>(); // Map<EventA, S_A>
    Map<Long, Map<Long, Double>> minWeightsSums = new HashMap<>(); // Map<Event1, Map<Event2, S_min>>

    @Override
    public List<EventSimilarityAvro> calculateChangedSimilarities(UserActionAvro action) {
        final var eventId = action.getEventId();
        final var newWeight = getActionWeight(action);

        final Double currentWeight = weightsByUsers
                .computeIfAbsent(eventId, e -> new HashMap<>())
                .getOrDefault(action.getUserId(), 0.0);

        // No need to recalculate or update data
        if (currentWeight >= newWeight) return List.of();

        // update weights of the action
        weightsByUsers
                .computeIfAbsent(eventId, e -> new HashMap<>())
                .put(action.getUserId(), newWeight);

        final var newEventSum = weightsSums.getOrDefault(eventId, 0.0) - currentWeight + newWeight;
        weightsSums.put(eventId, newEventSum);

        // filter events by the user interactions
        Map<Long, Double> eventsToRecalculate = weightsByUsers.entrySet().stream()
                .filter(e -> !e.getKey().equals(eventId) && e.getValue().containsKey(action.getUserId()))
                .collect(Collectors.toMap(Map.Entry::getKey, t -> t.getValue().get(action.getUserId())));

        List<EventSimilarityAvro> similarities = new ArrayList<>();

        for (Map.Entry<Long, Double> event2 : eventsToRecalculate.entrySet()) {
            double minSum = getMinWeights(eventId, event2.getKey());
            double deltaMin = Math.min(newWeight, event2.getValue()) - Math.min(currentWeight, event2.getValue());
            if (deltaMin != 0) {
                minSum += deltaMin;
                putMinWeights(eventId, event2.getKey(), minSum);
            }

            double event2Sum = weightsSums.get(event2.getKey());
            float score = (float) (minSum / Math.sqrt(newEventSum) / Math.sqrt(event2Sum));

            similarities.add(
                    EventSimilarityAvro.newBuilder()
                            .setEventA(Math.min(eventId, event2.getKey()))
                            .setEventB(Math.max(eventId, event2.getKey()))
                            .setTimestamp(action.getTimestamp())
                            .setScore(score)
                            .build()
            );
        }

        return similarities;
    }

    private void putMinWeights(long eventA, long eventB, double sum) {
        long first  = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        minWeightsSums
                .computeIfAbsent(first, e -> new HashMap<>())
                .put(second, sum);
    }

    private double getMinWeights(long eventA, long eventB) {
        long first  = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        return minWeightsSums
                .computeIfAbsent(first, e -> new HashMap<>())
                .getOrDefault(second, 0.0);
    }

    private double getActionWeight(UserActionAvro action) {
        return switch (action.getActionType()) {
            case VIEW -> 0.4;
            case REGISTER -> 0.8;
            case LIKE -> 1.0;
        };
    }
}
