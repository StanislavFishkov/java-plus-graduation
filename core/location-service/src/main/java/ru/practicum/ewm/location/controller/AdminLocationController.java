package ru.practicum.ewm.location.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.dto.location.LocationDto;
import ru.practicum.ewm.common.dto.location.NewLocationDto;
import ru.practicum.ewm.common.dto.location.UpdateLocationAdminRequestDto;
import ru.practicum.ewm.common.feignclient.LocationClient;
import ru.practicum.ewm.location.service.LocationService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/locations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminLocationController implements LocationClient {
    private final LocationService locationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public LocationDto addLocation(@RequestBody @Valid NewLocationDto newLocationDto) {
        log.info("POST /admin/locations with body({})", newLocationDto);
        return locationService.addLocation(newLocationDto);
    }

    @PatchMapping("/{locationId}")
    @Override
    public LocationDto updateLocation(@PathVariable(name = "locationId") Long locationId,
                                      @RequestBody @Valid UpdateLocationAdminRequestDto updateLocationAdminRequestDto) {
        log.info("PATCH /admin/locations with body({})", updateLocationAdminRequestDto);
        return locationService.updateLocation(locationId, updateLocationAdminRequestDto);
    }

    @DeleteMapping("/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void delete(@PathVariable(name = "locationId") Long locationId) {
        log.info("DELETE /admin/locations/{locationId} locationId = {})", locationId);
        locationService.delete(locationId);
    }

    @PutMapping
    @Override
    public LocationDto createLocationByCoordinates(@RequestParam double lat, @RequestParam double lon) {
        log.info("PUT /admin/locations with params (lat = {}, lon = {})", lat, lon);
        return locationService.createLocationByCoordinates(lat, lon);
    }

    @GetMapping("/{locationId}")
    @Override
    public LocationDto getById(@PathVariable(name = "locationId") Long locationId) {
        log.info("GET /admin/locations/{locationId} locationId = {})", locationId);
        return locationService.getById(locationId);
    }

    @GetMapping("/ids")
    @Override
    public List<LocationDto> getByIds(@RequestParam List<Long> locationIds) {
        log.info("GET /admin/locations/ids with params (locationIds = {})", locationIds);
        return locationService.getByIds(locationIds);
    }

    @GetMapping("/coordinates")
    @Override
    public List<LocationDto> getByCoordinatesAndRadius(@RequestParam double lat,
                                                       @RequestParam double lon,
                                                       @RequestParam double radius) {
        log.info("GET /admin/locations/ids with params (lat = {}, lon = {}, radius = {},)", lat, lon, radius);
        return locationService.getByCoordinatesAndRadius(lat, lon, radius);
    }
}
