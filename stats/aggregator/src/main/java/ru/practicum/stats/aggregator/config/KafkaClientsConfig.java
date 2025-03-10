package ru.practicum.stats.aggregator.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
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

    @Bean
    public KafkaConsumer<String, UserActionAvro> consumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServers());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaProperties.KEY_DESERIALIZER_CLASS);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProperties.USER_ACTION_DESERIALIZER_CLASS);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.consumerGroupId());

        return new KafkaConsumer<>(config);
    }

    @Bean
    public KafkaProducer<String, EventSimilarityAvro> producer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaProperties.KEY_SERIALIZER_CLASS);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaProperties.VALUE_SERIALIZER_CLASS);

        return new KafkaProducer<>(config);
    }
}