package ru.practicum.stats.analyzer.config;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.practicum.ewm.stats.avro.EventSimilarityDeserializer;
import ru.practicum.ewm.stats.avro.UserActionDeserializer;

@ConfigurationProperties(prefix = "analyzer.kafka")
public record KafkaProperties(String bootstrapServers, String userActionsConsumerGroupId, String eventsSimilarityConsumerGroupId) {
    public static final Class<?> KEY_DESERIALIZER_CLASS = StringDeserializer.class;
    public static final Class<?> USER_ACTION_DESERIALIZER_CLASS = UserActionDeserializer.class;
    public static final Class<?> EVENT_SIMILARITY_DESERIALIZER_CLASS = EventSimilarityDeserializer.class;
}