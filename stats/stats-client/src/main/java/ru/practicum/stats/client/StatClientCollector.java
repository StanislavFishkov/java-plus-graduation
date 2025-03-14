package ru.practicum.stats.client;


import ru.practicum.grpc.stats.action.ActionTypeProto;

import java.time.Instant;

public interface StatClientCollector {
    void collectUserAction(long userId, long eventId, ActionTypeProto actionTypeProto, Instant timestamp);
}