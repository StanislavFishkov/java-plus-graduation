package ru.practicum.stats.analyzer.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserActionType {
    VIEW(0.4),
    REGISTER(0.8),
    LIKE(1.0);

    private final double weight;
}