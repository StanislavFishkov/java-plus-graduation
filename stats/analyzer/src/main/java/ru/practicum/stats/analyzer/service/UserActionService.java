package ru.practicum.stats.analyzer.service;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface UserActionService {
    void handleUserAction(UserActionAvro userActionAvro);
}