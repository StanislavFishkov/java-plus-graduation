package ru.practicum.stats.analyzer.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Properties;

@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@RequiredArgsConstructor
public class KafkaClientsConfig {
    private final KafkaProperties kafkaProperties;

    @Bean("userActionsConsumer")
    public KafkaConsumer<String, UserActionAvro> hubEventsConsumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServers());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaProperties.KEY_DESERIALIZER_CLASS);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProperties.USER_ACTION_DESERIALIZER_CLASS);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.userActionsConsumerGroupId());
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return new KafkaConsumer<>(config);
    }

    @Bean("eventsSimilarityConsumer")
    public KafkaConsumer<String, EventSimilarityAvro> snapshotsConsumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServers());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaProperties.KEY_DESERIALIZER_CLASS);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProperties.EVENT_SIMILARITY_DESERIALIZER_CLASS);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.eventsSimilarityConsumerGroupId());
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return new KafkaConsumer<>(config);
    }
}