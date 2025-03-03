package ru.practicum.ewm.common.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.dto.event.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.common.dto.event.EventRequestStatusUpdateResultDto;
import ru.practicum.ewm.common.dto.participationrequest.ParticipationRequestDto;
import ru.practicum.ewm.common.model.participationrequest.ParticipationRequestStatus;

import java.util.List;
import java.util.Map;

@Validated
@FeignClient(name = "participationrequest-service", path = "/admin/requests")
public interface ParticipationRequestClient {
    @GetMapping
    ParticipationRequestDto getById(@RequestParam Long requestId);

    @GetMapping("/ids")
    List<ParticipationRequestDto> getByIds(@RequestParam List<Long> requestIds);

    @GetMapping("/event")
    List<ParticipationRequestDto> getByEventId(@RequestParam Long eventId,
                                               @RequestParam(required = false) ParticipationRequestStatus status);

    @PostMapping("/event")
    EventRequestStatusUpdateResultDto updateEventRequestsStatus(@RequestParam Long eventId,
                                                                @RequestParam Integer participantsLimit,
                                                                @RequestBody EventRequestStatusUpdateRequestDto statusUpdateRequest);

    @GetMapping("/event/confirmed/count")
    Map<Long, Long> getConfirmedCountByEventIds(@RequestParam List<Long> eventIds);
}
