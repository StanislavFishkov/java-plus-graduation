package ru.practicum.stats.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_actions")
@Getter
@Setter
@ToString(of = {"id", "eventId", "type", "timestamp"})
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long eventId;

    @Enumerated(EnumType.STRING)
    private UserActionType type;

    private LocalDateTime timestamp;
}