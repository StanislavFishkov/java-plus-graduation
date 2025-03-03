package ru.practicum.ewm.event.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.dto.event.EventFullDto;
import ru.practicum.ewm.common.dto.event.EventAdminFilterParamsDto;
import ru.practicum.ewm.common.dto.event.UpdateEventAdminRequestDto;
import ru.practicum.ewm.common.feignclient.EventClient;
import ru.practicum.ewm.event.event.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class AdminEventController implements EventClient {
    private final EventService eventService;

    @PatchMapping("/{eventId}")
    @Override
    public EventFullDto update(@PathVariable Long eventId,
                               @Valid @RequestBody UpdateEventAdminRequestDto updateEventAdminRequestDto) {
        return eventService.update(eventId, updateEventAdminRequestDto);
    }

    @GetMapping
    @Override
    public List<EventFullDto> get(@Valid EventAdminFilterParamsDto filters,
                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                  @Positive @RequestParam(defaultValue = "10") int size) {
        return eventService.get(filters, from, size);
    }

    @GetMapping("/location")
    @Override
    public boolean existsByLocationId(@RequestParam Long locationId) {
        return eventService.existsByLocationId(locationId);
    }

    @GetMapping("/{eventId}")
    @Override
    public EventFullDto getById(@PathVariable Long eventId) {
        return eventService.getById(eventId);
    }
}