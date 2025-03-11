package ru.practicum.stats.analyzer.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.stats.analyzer.service.EventSimilarityService;
import ru.practicum.stats.analyzer.config.AnalyzerTopics;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@EnableConfigurationProperties(AnalyzerTopics.class)
@RequiredArgsConstructor
public class EventSimilarityProcessor {
    private final KafkaConsumer<String, EventSimilarityAvro> consumer;
    private final AnalyzerTopics analyzerTopics;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private final EventSimilarityService eventSimilarityService;

    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(analyzerTopics.eventsSimilarityTopic()));

            // Poll Loop
            while (true) {
                ConsumerRecords<String, EventSimilarityAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                int count = 0;
                for (ConsumerRecord<String, EventSimilarityAvro> record : records) {
                    handleRecord(record);

                    manageOffsets(record, count);
                    count++;
                }
                // fix max offsets
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Analyzer: error during consuming loop from sensor snapshots topic", e);
        } finally {
            try {
                log.info("Analyzer: commit offsets before closing sensor snapshots consumer");
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Analyzer: close sensor snapshots consumer");
                consumer.close();
            }
        }
    }

    private void manageOffsets(ConsumerRecord<String, EventSimilarityAvro> record, int count) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Analyzer: error during fixing offsets for sensor snapshots topic: {}", offsets, exception);
                }
            });
        }
    }

    private void handleRecord(ConsumerRecord<String, EventSimilarityAvro> record) throws InterruptedException {
        log.info("Analyzer: record read. topic = {}, partition = {}, offset = {}, value: {}",
                record.topic(), record.partition(), record.offset(), record.value());
        eventSimilarityService.handleEventSimilarity(record.value());
    }
}