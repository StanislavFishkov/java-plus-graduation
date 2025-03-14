package ru.practicum.ewm.participationrequest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.common.dto.event.EventFullDto;
import ru.practicum.ewm.common.dto.event.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.common.dto.event.EventRequestStatusUpdateResultDto;
import ru.practicum.ewm.common.dto.participationrequest.ParticipationRequestDto;
import ru.practicum.ewm.common.feignclient.EventClient;
import ru.practicum.ewm.common.feignclient.UserClient;
import ru.practicum.ewm.common.model.participationrequest.ParticipationRequestStatus;
import ru.practicum.grpc.stats.action.ActionTypeProto;
import ru.practicum.stats.client.StatClientCollector;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipationRequestFacadeImpl implements ParticipationRequestFacade {
    private final ParticipationRequestService participationRequestService;
    private final UserClient userClient;
    private final EventClient eventClient;
    private final StatClientCollector statClientCollector;

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        userClient.getById(userId);
        EventFullDto event = eventClient.getById(eventId);

        ParticipationRequestDto participationRequestDto = participationRequestService.create(userId, event);

        statClientCollector.collectUserAction(eventId, userId, ActionTypeProto.ACTION_REGISTER, Instant.now());

        return participationRequestDto;
    }

    @Override
    public List<ParticipationRequestDto> get(Long userId) {
        userClient.getById(userId);

        return participationRequestService.get(userId);
    }

    @Override
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        userClient.getById(userId);

        return participationRequestService.cancel(userId, requestId);
    }

    @Override
    public ParticipationRequestDto getById(Long requestId) {
        return participationRequestService.getById(requestId);
    }

    @Override
    public List<ParticipationRequestDto> getByIds(List<Long> requestIds) {
        return participationRequestService.getByIds(requestIds);
    }

    @Override
    public List<ParticipationRequestDto> getByEventId(Long eventId, ParticipationRequestStatus status) {
        return participationRequestService.getByEventId(eventId, status);
    }

    @Override
    public EventRequestStatusUpdateResultDto updateEventRequestsStatus(Long eventId, Integer participantsLimit,
                                                                       EventRequestStatusUpdateRequestDto statusUpdateRequest) {
        return participationRequestService.updateEventRequestsStatus(eventId, participantsLimit, statusUpdateRequest);
    }

    @Override
    public Map<Long, Long> getConfirmedCountByEventIds(List<Long> eventIds) {
        return participationRequestService.getConfirmedCountByEventIds(eventIds);
    }
}