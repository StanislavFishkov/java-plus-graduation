package ru.practicum.stats.collector.service;

import ru.practicum.grpc.stats.action.UserActionProto;

public interface EventService {
    void collectUserAction(UserActionProto action);
}