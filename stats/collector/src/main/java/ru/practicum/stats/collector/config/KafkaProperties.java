package ru.practicum.stats.collector.config;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.practicum.ewm.stats.avro.AvroSerializer;

@ConfigurationProperties(prefix = "collector.kafka")
public record KafkaProperties(String bootstrapServers) {
    public static final Class<?> KEY_SERIALIZER_CLASS = StringSerializer.class;
    public static final Class<?> VALUE_SERIALIZER_CLASS = AvroSerializer.class;
}