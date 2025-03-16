package ru.practicum.stats.collector.mapper;

import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.grpc.stats.action.UserActionProto;

public interface UserActionMapper {
    UserActionAvro toAvro(UserActionProto event);
}
