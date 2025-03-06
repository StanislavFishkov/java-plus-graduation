package ru.practicum.ewm.event.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.common.dto.compilation.CompilationDto;
import ru.practicum.ewm.common.dto.compilation.CompilationRequestDto;
import ru.practicum.ewm.common.dto.compilation.UpdateCompilationRequestDto;
import ru.practicum.ewm.event.compilation.model.Compilation;
import ru.practicum.ewm.common.dto.event.EventShortDto;
import ru.practicum.ewm.event.event.model.Event;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {

    @Mapping(target = "events", source = "events")
    @Mapping(target = "id", ignore = true)
    Compilation toEntity(CompilationRequestDto dto, List<Event> events);

    @Mapping(target = "events", source = "eventShortDtoList")
    CompilationDto toDto(Compilation entity, List<EventShortDto> eventShortDtoList);

    @Mapping(target = "id", source = "compId")
    @Mapping(target = "events", source = "events")
    void update(UpdateCompilationRequestDto update, Long compId, List<Event> events,
                @MappingTarget Compilation destination);
}
