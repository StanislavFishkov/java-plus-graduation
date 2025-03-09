package ru.practicum.stats.collector.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.grpc.stats.action.UserActionProto;

import static ru.practicum.stats.collector.util.TimestampProto.toInstant;

@Component
public class UserActionMapperImpl implements UserActionMapper {
    @Override
    public UserActionAvro toAvro(UserActionProto action) {
        return UserActionAvro.newBuilder()
                .setUserId(action.getUserId())
                .setEventId(action.getEventId())
                .setActionType(
                        switch (action.getActionType()) {
                            case ACTION_VIEW -> ActionTypeAvro.VIEW;
                            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
                            case ACTION_LIKE -> ActionTypeAvro.LIKE;
                            case UNRECOGNIZED -> null;
                        })
                .setTimestamp(toInstant(action.getTimestamp()))
                .build();
    }
}