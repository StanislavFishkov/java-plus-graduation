package ru.practicum.ewm.participationrequest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.dto.participationrequest.ParticipationRequestDto;
import ru.practicum.ewm.participationrequest.service.ParticipationRequestFacade;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class PrivateParticipationRequestController {
    private final ParticipationRequestFacade participationRequestFacade;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto create(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("POST /users/{}/requests with params(eventId {})", userId, eventId);
        return participationRequestFacade.create(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> get(@PathVariable Long userId) {
        log.info("GET /users/{}/requests", userId);
        return participationRequestFacade.get(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("PATCH /users/{}/requests/{}/cancel", userId, requestId);
        return participationRequestFacade.cancel(userId, requestId);
    }
}