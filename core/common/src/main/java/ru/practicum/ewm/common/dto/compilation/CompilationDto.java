package ru.practicum.ewm.common.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.common.dto.event.EventShortDto;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventShortDto> events;
}