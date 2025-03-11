package ru.practicum.stats.analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.stats.analyzer.starter.EventSimilarityProcessor;
import ru.practicum.stats.analyzer.starter.UserActionProcessor;

@SpringBootApplication
public class Analyzer {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Analyzer.class, args);

        final UserActionProcessor userActionProcessor = context.getBean(UserActionProcessor.class);
        EventSimilarityProcessor eventSimilarityProcessor = context.getBean(EventSimilarityProcessor.class);

        // processing in the separate thread
        Thread userActionThread = new Thread(userActionProcessor);
        userActionThread.setName("userActionHandlerThread");
        userActionThread.start();

        // processing in the current thread
        eventSimilarityProcessor.start();
    }
}