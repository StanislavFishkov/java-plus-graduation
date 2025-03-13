package ru.practicum.stats.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@IdClass(EventSimilarityId.class)
@Table(name = "event_similarities")
@Getter
@Setter
@ToString(of = {"eventA", "eventB", "score"})
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EventSimilarity {
    @Id
    @Column(name = "event_a")
    private Long eventA;

    @Id
    @Column(name = "event_b")
    private Long eventB;

    private Float score;
}