package ru.practicum.ewm.event.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.dto.event.*;
import ru.practicum.ewm.event.event.service.EventFacade;
import ru.practicum.ewm.common.dto.participationrequest.ParticipationRequestDto;

import java.util.List;


@RequiredArgsConstructor
@Validated
@RestController
@Slf4j
public class PrivateEventController {
    private final EventFacade eventFacade;

    //Добавление нового события
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/users/{userId}/events")
    public EventFullDto createEvent(@PathVariable("userId") Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        return eventFacade.addEvent(userId, newEventDto);
    }

    //Получение событий, добавленных текущим пользователем
    @GetMapping(path = "/users/{userId}/events")
    public List<EventShortDto> getEvent(@PathVariable("userId") Long userId,
                                        @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                        @Positive @RequestParam(defaultValue = "10") int size) {
        return eventFacade.getEventsByUserId(userId, from, size);
    }

    //Получение полной информации о событии добавленном текущим пользователем
    @GetMapping(path = "/users/{userId}/events/{eventId}")
    public EventFullDto getEvent(@PathVariable("userId") Long userId, @PathVariable("eventId") Long eventId) {
        return eventFacade.getEventById(userId, eventId);
    }

    //Изменение события добавленного текущим пользователем
    @PatchMapping(path = "/users/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable("userId") Long userId,
                                    @PathVariable("eventId") Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequestDto eventUpdateDto) {
        return eventFacade.updateEvent(userId, eventId, eventUpdateDto);
    }

    //Получение информации о запросах на участие в событии текущего пользователя
    @GetMapping(path = "/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequests(@PathVariable("userId") Long userId,
                                                                  @PathVariable("eventId") Long eventId) {
        return eventFacade.getEventAllParticipationRequests(userId, eventId);
    }

    //Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
    @PatchMapping(path = "/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResultDto updatedEventRequestStatus(@PathVariable("userId") Long userId,
                                                                       @PathVariable("eventId") Long eventId,
                                                                       @RequestBody EventRequestStatusUpdateRequestDto request) {
        return eventFacade.changeEventState(userId, eventId, request);
    }

}