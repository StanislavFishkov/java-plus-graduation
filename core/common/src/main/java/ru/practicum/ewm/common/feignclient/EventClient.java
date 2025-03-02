package ru.practicum.ewm.common.feignclient;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.dto.event.EventAdminFilterParamsDto;
import ru.practicum.ewm.common.dto.event.EventFullDto;
import ru.practicum.ewm.common.dto.event.UpdateEventAdminRequestDto;

import java.util.List;

@Validated
@FeignClient(name = "main-service", path = "/admin/events")
public interface EventClient {
    @PatchMapping("/{eventId}")
    EventFullDto update(@PathVariable Long eventId,
                        @Valid @RequestBody UpdateEventAdminRequestDto updateEventAdminRequestDto);

    @GetMapping
    List<EventFullDto> get(@Valid EventAdminFilterParamsDto filters,
                           @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                           @Positive @RequestParam(defaultValue = "10") int size);

    @GetMapping("/location")
    boolean existsByLocationId(@RequestParam Long locationId);
}
