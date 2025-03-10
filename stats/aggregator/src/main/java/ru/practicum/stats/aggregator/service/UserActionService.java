package ru.practicum.stats.aggregator.service;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.List;

public interface UserActionService {
    List<EventSimilarityAvro> calculateChangedSimilarities(UserActionAvro action);
}