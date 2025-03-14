package ru.practicum.stats.aggregator.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.aggregator.config.AggregatorTopics;
import ru.practicum.stats.aggregator.config.KafkaProperties;
import ru.practicum.stats.aggregator.service.UserActionService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@EnableConfigurationProperties({AggregatorTopics.class, KafkaProperties.class})
@RequiredArgsConstructor
public class AggregationStarter {
    private final KafkaConsumer<String, UserActionAvro> consumer;
    private final KafkaProducer<String, EventSimilarityAvro> producer;
    private final AggregatorTopics aggregatorTopics;
    private final UserActionService userActionService;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    public void start() {
        // register wakeup hook
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(aggregatorTopics.userActionsTopic()));

            while (true) {
                ConsumerRecords<String, UserActionAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                int count = 0;
                for (ConsumerRecord<String, UserActionAvro> record : records) {
                    handleRecord(record);

                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
            // resources will be closed in finally
        } catch (Exception e) {
            log.error("Error occurred while polling user actions topic.", e);
        } finally {
            try {
                log.info("Flush current messages from producer queue into kafka");
                producer.flush();
                log.info("Commit offsets synchronously on closing");
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Close producer");
                producer.close();
                log.info("Close consumer");
                consumer.close();
            }
        }
    }

    private void manageOffsets(ConsumerRecord<String, UserActionAvro> record, int count,
                                      KafkaConsumer<String, UserActionAvro> consumer) {
        // update current offset for topic-partition
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Error during fixing offsets: {}", offsets, exception);
                }
            });
        }
    }

    private void handleRecord(ConsumerRecord<String, UserActionAvro> record) throws InterruptedException {
        log.info("topic = {}, partition = {}, offset = {}, value: {}",
                record.topic(), record.partition(), record.offset(), record.value());

        List<EventSimilarityAvro> similarities = userActionService.calculateChangedSimilarities(record.value());
        for (EventSimilarityAvro similarity : similarities) {
            ProducerRecord<String, EventSimilarityAvro> similarityRecord =
                    new ProducerRecord<>(aggregatorTopics.eventsSimilarityTopic(), null,
                            similarity.getTimestamp().toEpochMilli(), Long.toString(similarity.getEventA()), similarity);

            producer.send(similarityRecord);
            log.info("Kafka producer sent events similarity message {}\n", similarityRecord);
        }
    }
}