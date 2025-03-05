package ru.practicum.ewm.participationrequest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.dto.event.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.common.dto.event.EventRequestStatusUpdateResultDto;
import ru.practicum.ewm.common.dto.participationrequest.ParticipationRequestDto;
import ru.practicum.ewm.common.feignclient.ParticipationRequestClient;
import ru.practicum.ewm.common.model.participationrequest.ParticipationRequestStatus;
import ru.practicum.ewm.participationrequest.service.ParticipationRequestFacade;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/requests")
public class AdminParticipationRequestController implements ParticipationRequestClient {
    private final ParticipationRequestFacade participationRequestFacade;

    @GetMapping
    @Override
    public ParticipationRequestDto getById(@RequestParam Long requestId) {
        log.info("GET /admin/requests with params(requestId {})", requestId);
        return participationRequestFacade.getById(requestId);
    }

    @GetMapping("/ids")
    @Override
    public List<ParticipationRequestDto> getByIds(@RequestParam List<Long> requestIds) {
        log.info("GET /admin/requests/ids with params (requestIds {})", requestIds);
        return participationRequestFacade.getByIds(requestIds);
    }

    @GetMapping("/event")
    @Override
    public List<ParticipationRequestDto> getByEventId(@RequestParam Long eventId,
                                                      @RequestParam(required = false) ParticipationRequestStatus status) {
        log.info("GET /admin/requests/event with params (eventId {}, status {})", eventId, status);
        return participationRequestFacade.getByEventId(eventId, status);
    }

    @PostMapping("/event")
    @Override
    public EventRequestStatusUpdateResultDto updateEventRequestsStatus(@RequestParam Long eventId,
                                                                       @RequestParam Integer participantsLimit,
                                                                       @RequestBody EventRequestStatusUpdateRequestDto statusUpdateRequest) {
        log.info("PATCH /admin/requests/event with params (eventId {}, participantsLimit {}, statusUpdateRequest {})",
                eventId, participantsLimit, statusUpdateRequest);
        return participationRequestFacade.updateEventRequestsStatus(eventId, participantsLimit, statusUpdateRequest);
    }

    @GetMapping("/event/confirmed/count")
    @Override
    public Map<Long, Long> getConfirmedCountByEventIds(@RequestParam List<Long> eventIds) {
        log.info("GET /admin/requests/confirmed/count with params (eventIds {})", eventIds);
        return participationRequestFacade.getConfirmedCountByEventIds(eventIds);
    }
}