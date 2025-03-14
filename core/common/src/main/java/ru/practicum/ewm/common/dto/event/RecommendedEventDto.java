package ru.practicum.ewm.common.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendedEventDto {
    private Long eventId;
    private Float score;
}