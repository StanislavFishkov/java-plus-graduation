package ru.practicum.ewm.event.event.service;

import ru.practicum.ewm.common.dto.event.*;
import ru.practicum.ewm.common.dto.location.LocationDto;
import ru.practicum.ewm.common.dto.user.UserShortDto;
import ru.practicum.ewm.event.event.model.Event;

import java.util.List;

public interface EventService {
    EventFullDto addEvent(UserShortDto userShortDto, NewEventDto newEventDto, LocationDto locationDto);

    List<Event> getEventsByUserId(Long id, int from, int size);

    Event updateEvent(Long userId, Long eventId, UpdateEventUserRequestDto eventUpdateDto, LocationDto locationDto);

    Event update(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto, LocationDto locationDto);

    Event getById(Long eventId);

    List<Event> get(EventAdminFilterParamsDto filters, int from, int size);

    List<Event> get(EventPublicFilterParamsDto filters, int from, int size, List<Long> locationsIds);

    boolean existsByLocationId(Long locationId);

    Event getEventByIdAndInitiatorId(Long eventId, Long initiatorId);
}