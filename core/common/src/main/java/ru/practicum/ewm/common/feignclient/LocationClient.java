package ru.practicum.ewm.common.feignclient;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.dto.location.LocationDto;
import ru.practicum.ewm.common.dto.location.NewLocationDto;
import ru.practicum.ewm.common.dto.location.UpdateLocationAdminRequestDto;

import java.util.List;

@FeignClient(name = "location-service", path = "/admin/locations")
public interface LocationClient {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    LocationDto addLocation(@RequestBody @Valid NewLocationDto newLocationDto);

    @PatchMapping("/{locationId}")
    LocationDto updateLocation(@PathVariable(name = "locationId") Long locationId,
                               @RequestBody @Valid UpdateLocationAdminRequestDto updateLocationAdminRequestDto);

    @DeleteMapping("/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable(name = "locationId") Long locationId);

    @PutMapping
    LocationDto createLocationByCoordinates(@RequestParam double lat, @RequestParam double lon);

    @GetMapping("/{locationId}")
    LocationDto getById(@PathVariable(name = "locationId") Long locationId);

    @GetMapping("/ids")
    List<LocationDto> getByIds(@RequestParam List<Long> locationIds);

    @GetMapping("/coordinates")
    List<LocationDto> getByCoordinatesAndRadius(@RequestParam double lat,
                                                @RequestParam double lon,
                                                @RequestParam double radius);
}
