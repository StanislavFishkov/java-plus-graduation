package ru.practicum.ewm.location.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.dto.location.LocationDto;
import ru.practicum.ewm.common.dto.location.NewLocationDto;
import ru.practicum.ewm.common.dto.location.UpdateLocationAdminRequestDto;
import ru.practicum.ewm.common.error.exception.ConflictDataException;
import ru.practicum.ewm.common.error.exception.NotFoundException;
import ru.practicum.ewm.common.feignclient.EventClient;
import ru.practicum.ewm.common.util.PagingUtil;
import ru.practicum.ewm.location.mapper.LocationMapper;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.repository.LocationRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocationServiceImpl implements  LocationService {
    LocationRepository locationRepository;
    LocationMapper locationMapper;
    EventClient eventClient;

    @Override
    @Transactional(readOnly = true)
    public List<LocationDto> getLocations(Integer from, Integer size) {
        log.info("start getLocations by from {} size {}", from, size);
        return locationRepository.findAll(PagingUtil.pageOf(from, size)).stream()
                .map(locationMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LocationDto getById(Long locationId) {
        log.info("getById params: id = {}", locationId);
        Location location = locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException(
                String.format("Локация с ид %s не найдена", locationId))
        );
        log.info("getById result location = {}", location);
        return locationMapper.toDto(location);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationDto> getByIds(List<Long> locationIds) {
        log.info("getByIds params: locationIds = {}", locationIds);
        List<Location> locations = locationRepository.findAllById(locationIds);
        log.info("getByIds result locations = {}", locations);
        return locationMapper.toDto(locations);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationDto> getByCoordinatesAndRadius(double lat, double lon, double radius) {
        log.info("getByCoordinatesAndRadius params: lat = {}, lon = {}, radius = {}", lat, lon, radius);
        List<Location> locations = locationRepository.findAllByCoordinatesAndRadius(lat, lon, radius);
        log.info("getByCoordinatesAndRadius result locations = {}", locations);
        return locationMapper.toDto(locations);
    }

    @Override
    @Transactional
    public LocationDto addLocation(NewLocationDto newLocationDto) {
        Location location = locationRepository.save(locationMapper.toLocation(newLocationDto));
        log.info("Location is created: {}", location);
        return locationMapper.toDto(location);
    }

    @Override
    @Transactional
    public LocationDto createLocationByCoordinates(double lat, double lon) {
        Optional<Location> foundLocation = locationRepository.findByLatAndLon(lat, lon);

        Location location;
        if (foundLocation.isPresent()) {
            location = foundLocation.get();
        } else {
            location = Location.builder().lat(lat).lon(lon).build();
            location = locationRepository.save(location);
            log.info("Location is created by coordinates: {}", location);
        }

        return locationMapper.toDto(location);
    }

    @Override
    @Transactional
    public LocationDto updateLocation(Long locationId, UpdateLocationAdminRequestDto updateLocationAdminRequestDto) {
        log.info("start updateLocation");
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Location with id " + locationId + " not found"));
        location = locationRepository.save(locationMapper.update(location, updateLocationAdminRequestDto));
        log.info("Location is updated: {}", location);
        return locationMapper.toDto(location);
    }

    @Override
    public void delete(Long locationId) {
        if (eventClient.existsByLocationId(locationId))
            throw new ConflictDataException("Location with id %s can't be deleted, because of existing events".formatted(locationId));

        locationRepository.deleteById(locationId);
        log.info("Location deleted with id: {}", locationId);
    }
}
