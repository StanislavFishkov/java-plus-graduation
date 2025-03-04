package ru.practicum.ewm.location.service;

import ru.practicum.ewm.common.dto.location.LocationDto;
import ru.practicum.ewm.common.dto.location.NewLocationDto;
import ru.practicum.ewm.common.dto.location.UpdateLocationAdminRequestDto;

import java.util.List;

public interface LocationFacade {

    List<LocationDto> getLocations(Integer from, Integer size);

    LocationDto getById(Long locationId);

    List<LocationDto> getByIds(List<Long> locationIds);

    List<LocationDto> getByCoordinatesAndRadius(double lat, double lon, double radius);

    LocationDto addLocation(NewLocationDto newLocationDto);

    LocationDto createLocationByCoordinates(double lat, double lon);

    LocationDto updateLocation(Long locationId, UpdateLocationAdminRequestDto updateLocationAdminRequestDto);

    void delete(Long locationId);
}
