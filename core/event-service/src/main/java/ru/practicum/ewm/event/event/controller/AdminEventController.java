package ru.practicum.ewm.event.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.dto.event.EventFullDto;
import ru.practicum.ewm.common.dto.event.EventAdminFilterParamsDto;
import ru.practicum.ewm.common.dto.event.UpdateEventAdminRequestDto;
import ru.practicum.ewm.common.feignclient.EventClient;
import ru.practicum.ewm.event.event.service.EventFacade;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/admin/events")
public class AdminEventController implements EventClient {
    private final EventFacade eventFacade;

    @PatchMapping("/{eventId}")
    @Override
    public EventFullDto update(@PathVariable Long eventId,
                               @Valid @RequestBody UpdateEventAdminRequestDto updateEventAdminRequestDto) {
        return eventFacade.update(eventId, updateEventAdminRequestDto);
    }

    @GetMapping
    @Override
    public List<EventFullDto> get(@Valid EventAdminFilterParamsDto filters,
                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                  @Positive @RequestParam(defaultValue = "10") int size) {
        return eventFacade.get(filters, from, size);
    }

    @GetMapping("/location")
    @Override
    public boolean existsByLocationId(@RequestParam Long locationId) {
        return eventFacade.existsByLocationId(locationId);
    }

    @GetMapping("/{eventId}")
    @Override
    public EventFullDto getById(@PathVariable Long eventId) {
        return eventFacade.getById(eventId);
    }
}