package ru.practicum.ewm.participationrequest.service;

import ru.practicum.ewm.common.dto.event.EventFullDto;
import ru.practicum.ewm.common.dto.event.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.common.dto.event.EventRequestStatusUpdateResultDto;
import ru.practicum.ewm.common.dto.participationrequest.ParticipationRequestDto;
import ru.practicum.ewm.common.model.participationrequest.ParticipationRequestStatus;

import java.util.List;
import java.util.Map;

public interface ParticipationRequestService {
    ParticipationRequestDto create(Long userId, EventFullDto event);

    List<ParticipationRequestDto> get(Long userId);

    ParticipationRequestDto cancel(Long userId, Long requestId);

    ParticipationRequestDto getById(Long requestId);

    List<ParticipationRequestDto> getByIds(List<Long> requestIds);

    List<ParticipationRequestDto> getByEventId(Long eventId, ParticipationRequestStatus status);

    EventRequestStatusUpdateResultDto updateEventRequestsStatus(Long eventId, Integer participantsLimit,
                                                                EventRequestStatusUpdateRequestDto statusUpdateRequest);

    Map<Long, Long> getConfirmedCountByEventIds(List<Long> eventIds);
}