package ru.practicum.ewm.location.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.dto.location.LocationDto;
import ru.practicum.ewm.location.service.LocationFacade;

import java.util.List;

@RestController
@RequestMapping(path = "/locations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicLocationController {
    private final LocationFacade locationService;

    @GetMapping
    public List<LocationDto> getLocations(@RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /locations?from={}&size={}", from, size);
        return locationService.getLocations(from, size);
    }

    @GetMapping("/{locationId}")
    public LocationDto getById(@PathVariable Long locationId) {
        log.info("GET /locations/{}", locationId);
        return locationService.getById(locationId);
    }
}
