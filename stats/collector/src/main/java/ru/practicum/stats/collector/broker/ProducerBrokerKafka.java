package ru.practicum.stats.collector.broker;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.practicum.stats.collector.config.KafkaProperties;

import java.util.Properties;

@Slf4j
@Component
@EnableConfigurationProperties(KafkaProperties.class)
public class ProducerBrokerKafka implements ProducerBroker {
    private final Producer<String, SpecificRecordBase> producer;

    public ProducerBrokerKafka(KafkaProperties kafkaProperties) {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaProperties.KEY_SERIALIZER_CLASS);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaProperties.VALUE_SERIALIZER_CLASS);

        this.producer = new KafkaProducer<>(config);
    }

    @Override
    public void send(String topic, Long timestamp, String key, SpecificRecordBase specificRecordBase) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, null, timestamp, key, specificRecordBase);
        producer.send(record);
        log.info("Kafka producer sent message {}", record);
    }

    @PreDestroy
    public void close() {
        producer.flush();
        producer.close();
    }
}