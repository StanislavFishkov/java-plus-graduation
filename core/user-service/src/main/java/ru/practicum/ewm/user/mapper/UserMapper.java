package ru.practicum.ewm.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.common.dto.user.UserDto;
import ru.practicum.ewm.common.dto.user.UserRequestDto;
import ru.practicum.ewm.common.dto.user.UserShortDto;
import ru.practicum.ewm.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toEntity(UserRequestDto dto);

    UserDto toDto(User entity);

    UserShortDto toShortDto(User entity);
}