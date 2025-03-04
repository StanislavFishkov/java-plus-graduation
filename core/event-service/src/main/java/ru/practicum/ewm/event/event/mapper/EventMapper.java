package ru.practicum.ewm.event.event.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.event.categories.model.Category;
import ru.practicum.ewm.common.dto.event.*;
import ru.practicum.ewm.common.dto.location.LocationDto;
import ru.practicum.ewm.common.dto.user.UserShortDto;
import ru.practicum.ewm.event.event.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "id", source = "event.id")
    EventShortDto toShortDto(Event event, UserShortDto initiator);

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "id", source = "event.id")
    EventFullDto toFullDto(Event event, UserShortDto initiator, LocationDto location);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiatorId", source = "userFromRequest")
    @Mapping(target = "locationId", source = "location.id")
    @Mapping(target = "participantLimit", defaultValue = "0")
    @Mapping(target = "paid", defaultValue = "false")
    @Mapping(target = "requestModeration", defaultValue = "true")
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    Event toEvent(NewEventDto newEventDto, Category category, Long userFromRequest, LocationDto location);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiatorId", ignore = true)
    @Mapping(target = "locationId", source = "location.id")
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event update(@MappingTarget Event event, UpdateEventUserRequestDto eventUpdateDto, LocationDto location);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiatorId", ignore = true)
    @Mapping(target = "locationId", source = "location.id")
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event update(@MappingTarget Event event, UpdateEventAdminRequestDto eventUpdateDto, Category category, LocationDto location);
}