package ru.practicum.stats.analyzer.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserActionType {
    VIEW(0.4f),
    REGISTER(0.8f),
    LIKE(1.0f);

    private final float weight;
}