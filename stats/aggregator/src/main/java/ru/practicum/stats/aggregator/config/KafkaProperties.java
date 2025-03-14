package ru.practicum.stats.aggregator.config;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.practicum.ewm.stats.avro.AvroSerializer;
import ru.practicum.ewm.stats.avro.UserActionDeserializer;

@ConfigurationProperties(prefix = "aggregator.kafka")
public record KafkaProperties(String bootstrapServers, String consumerGroupId) {
    public static final Class<?> KEY_SERIALIZER_CLASS = StringSerializer.class;
    public static final Class<?> KEY_DESERIALIZER_CLASS = StringDeserializer.class;
    public static final Class<?> VALUE_SERIALIZER_CLASS = AvroSerializer.class;
    public static final Class<?> USER_ACTION_DESERIALIZER_CLASS = UserActionDeserializer.class;
}