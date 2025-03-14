package ru.practicum.ewm.location.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.common.dto.location.LocationDto;
import ru.practicum.ewm.common.dto.location.NewLocationDto;
import ru.practicum.ewm.common.dto.location.UpdateLocationAdminRequestDto;
import ru.practicum.ewm.location.model.Location;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationDto toDto(Location location);

    List<LocationDto> toDto(List<Location> locations);

    @Mapping(target = "id", ignore = true)
    Location toLocation(NewLocationDto newLocationDto);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Location update(@MappingTarget Location location, UpdateLocationAdminRequestDto updateLocationAdminRequestDto);
}