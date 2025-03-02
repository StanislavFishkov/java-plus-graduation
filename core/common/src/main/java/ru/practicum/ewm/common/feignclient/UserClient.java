package ru.practicum.ewm.common.feignclient;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.dto.user.UserDto;
import ru.practicum.ewm.common.dto.user.UserRequestDto;
import ru.practicum.ewm.common.dto.user.UserShortDto;

import java.util.List;

@Validated
@FeignClient(name = "user-service", path = "/admin/users")
public interface UserClient {
    @GetMapping
    List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                           @Positive @RequestParam(defaultValue = "10") Integer size);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDto registerUser(@RequestBody @Valid UserRequestDto userRequestDto);

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable(name = "userId") Long userId);

    @GetMapping("/{userId}")
    UserShortDto getById(@PathVariable Long userId);

    @GetMapping("/ids")
    List<UserShortDto> getByIds(@RequestParam List<Long> userIds);
}