package ru.practicum.ewm.location.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.common.dto.location.LocationDto;
import ru.practicum.ewm.common.dto.location.NewLocationDto;
import ru.practicum.ewm.common.dto.location.UpdateLocationAdminRequestDto;
import ru.practicum.ewm.common.error.exception.ConflictDataException;
import ru.practicum.ewm.common.feignclient.EventClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocationFacadeImpl implements  LocationFacade {
    LocationService locationService;
    EventClient eventClient;

    @Override
    public List<LocationDto> getLocations(Integer from, Integer size) {
        return locationService.getLocations(from, size);
    }

    @Override
    public LocationDto getById(Long locationId) {
        return locationService.getById(locationId);
    }

    @Override
    public List<LocationDto> getByIds(List<Long> locationIds) {
        return locationService.getByIds(locationIds);
    }

    @Override
    public List<LocationDto> getByCoordinatesAndRadius(double lat, double lon, double radius) {
        return locationService.getByCoordinatesAndRadius(lat, lon, radius);
    }

    @Override
    public LocationDto addLocation(NewLocationDto newLocationDto) {
        return locationService.addLocation(newLocationDto);
    }

    @Override
    public LocationDto createLocationByCoordinates(double lat, double lon) {
        return locationService.createLocationByCoordinates(lat, lon);
    }

    @Override
    public LocationDto updateLocation(Long locationId, UpdateLocationAdminRequestDto updateLocationAdminRequestDto) {
        return locationService.updateLocation(locationId, updateLocationAdminRequestDto);
    }

    @Override
    public void delete(Long locationId) {
        if (eventClient.existsByLocationId(locationId))
            throw new ConflictDataException("Location with id %s can't be deleted, because of existing events".formatted(locationId));

        locationService.delete(locationId);
    }
}