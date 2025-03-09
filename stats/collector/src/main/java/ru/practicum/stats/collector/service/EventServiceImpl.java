package ru.practicum.stats.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.grpc.stats.action.UserActionProto;
import ru.practicum.stats.collector.broker.ProducerBroker;
import ru.practicum.stats.collector.config.CollectorTopics;

import ru.practicum.stats.collector.mapper.UserActionMapper;

import static ru.practicum.stats.collector.util.TimestampProto.toEpochMilli;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(CollectorTopics.class)
public class EventServiceImpl implements EventService {
    private final ProducerBroker producerBroker;
    private final CollectorTopics collectorTopics;
    private final UserActionMapper userActionMapper;

    @Override
    public void collectUserAction(UserActionProto action) {
        UserActionAvro eventAvro = userActionMapper.toAvro(action);
        log.info("Proto user action transformed to Avro: {}", eventAvro);
        producerBroker.send(collectorTopics.userActionsTopic(), toEpochMilli(action.getTimestamp()),
                Long.toString(action.getUserId()), eventAvro);
    }
}