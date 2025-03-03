package ru.practicum.ewm.event.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.common.dto.event.*;
import ru.practicum.ewm.common.dto.participationrequest.ParticipationRequestDto;

import java.util.List;

public interface EventService {
    EventFullDto addEvent(Long id, NewEventDto newEventDto);

    List<EventShortDto> getEventsByUserId(Long id, int from, int size);

    EventFullDto getEventById(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequestDto eventUpdateDto);

    EventFullDto update(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto);

    EventFullDto get(Long eventId, HttpServletRequest request);

    EventFullDto getById(Long eventId);

    List<EventFullDto> get(EventAdminFilterParamsDto filters, int from, int size);

    List<EventShortDto> get(EventPublicFilterParamsDto filters, int from, int size, HttpServletRequest request);

    List<ParticipationRequestDto> getEventAllParticipationRequests(Long eventId, Long userId);

    EventRequestStatusUpdateResultDto changeEventState(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequestDto requestStatusUpdateRequest);

    boolean existsByLocationId(Long locationId);
}