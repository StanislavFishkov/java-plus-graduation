package ru.practicum.stats.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@IdClass(UserActionId.class)
@Table(name = "user_actions")
@Getter
@Setter
@ToString(of = {"userId", "eventId", "type", "timestamp"})
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserAction {
    @Id
    private Long userId;

    @Id
    private Long eventId;

    @Enumerated(EnumType.STRING)
    private UserActionType type;

    private Float weight;

    private Instant timestamp;
}