package ru.practicum.ewm.event.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.dto.event.EventFullDto;
import ru.practicum.ewm.common.dto.event.EventPublicFilterParamsDto;
import ru.practicum.ewm.common.dto.event.EventShortDto;
import ru.practicum.ewm.common.dto.event.RecommendedEventDto;
import ru.practicum.ewm.event.event.service.EventFacade;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/events")
public class PublicEventController {
    private final EventFacade eventFacade;

    @GetMapping("/{id}")
    public EventFullDto get(@PathVariable("id") Long eventId,
                            @RequestHeader("X-EWM-USER-ID") Long userId) {
        return eventFacade.get(eventId, userId);
    }

    @GetMapping
    public List<EventShortDto> get(@Valid EventPublicFilterParamsDto filters,
                                   @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                   @Positive @RequestParam(defaultValue = "10") int size,
                                   HttpServletRequest request) {
        return eventFacade.get(filters, from, size, request);
    }

    @GetMapping("recommendations")
    public List<RecommendedEventDto> getRecommendations(@RequestHeader("X-EWM-USER-ID") Long userId,
                                                        @Positive @RequestParam(defaultValue = "10") int maxResults) {
        return eventFacade.getRecommendations(userId, maxResults);
    }

    @PutMapping("/{eventId}/like")
    public void like(@RequestHeader("X-EWM-USER-ID") Long userId,
                     @PathVariable("eventId") Long eventId) {
        eventFacade.like(userId, eventId);
    }
}