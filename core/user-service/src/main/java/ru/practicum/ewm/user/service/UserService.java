package ru.practicum.ewm.user.service;

import ru.practicum.ewm.common.dto.user.UserDto;
import ru.practicum.ewm.common.dto.user.UserRequestDto;
import ru.practicum.ewm.common.dto.user.UserShortDto;

import java.util.List;

public interface UserService {

    UserShortDto getById(Long userId);

    List<UserShortDto> getUsers(List<Long> ids);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserDto registerUser(UserRequestDto userRequestDto);

    void delete(Long userId);
}
