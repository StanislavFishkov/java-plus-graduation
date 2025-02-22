package ru.practicum.stats.client.error.exception;

public class StatsServerUnavailable extends RuntimeException {
    public StatsServerUnavailable(String message) {
        super(message);
    }

    public StatsServerUnavailable(String message, Throwable exception) {
        super(message, exception);
    }
}