package ru.practicum.ewm.participationrequest.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.dto.user.UserShortDto;
import ru.practicum.ewm.common.error.exception.ConflictDataException;
import ru.practicum.ewm.common.error.exception.NotFoundException;
import ru.practicum.ewm.common.feignclient.UserClient;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventStates;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.participationrequest.dto.ParticipationRequestDto;
import ru.practicum.ewm.participationrequest.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.participationrequest.model.ParticipationRequest;
import ru.practicum.ewm.participationrequest.model.ParticipationRequestStatus;
import ru.practicum.ewm.participationrequest.repository.ParticipationRequestRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestMapper participationRequestMapper;
    private final UserClient userClient;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ParticipationRequestDto create(Long userId, Long eventId) {
        checkAndGetUserById(userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("On part. request create - " +
                        "Event doesn't exist with id: " + eventId));

        if (!event.getState().equals(EventStates.PUBLISHED))
            throw new ConflictDataException("On part. request create - " +
                    "Event isn't published with id: " + eventId);


        if (event.getInitiatorId().equals(userId))
            throw new ConflictDataException(
                    String.format("On part. request create - " +
                            "Event with id %s has Requester with id %s as an initiator: ", eventId, userId));

        if (participationRequestRepository.existsByRequesterIdAndEvent(userId, event))
            throw new ConflictDataException(
                    String.format("On part. request create - " +
                            "Request by Requester with id %s and Event with id %s already exists: ", eventId, userId));

        if (event.getParticipantLimit() != 0) {
            long requestsCount = participationRequestRepository.countByEventAndStatusIn(event,
                    List.of(ParticipationRequestStatus.CONFIRMED));
            if (requestsCount >= event.getParticipantLimit())
                throw new ConflictDataException(
                        String.format("On part. request create - " +
                                "Event with id %s reached the limit of participants and User with id %s can't apply: ", eventId, userId));
        }

        ParticipationRequest createdParticipationRequest = participationRequestRepository.save(
                ParticipationRequest.builder()
                        .requesterId(userId)
                        .event(event)
                        .status(event.getParticipantLimit() != 0 && event.getRequestModeration() ?
                                ParticipationRequestStatus.PENDING : ParticipationRequestStatus.CONFIRMED)
                        .build()
        );
        log.info("Participation request is created: {}", createdParticipationRequest);
        return participationRequestMapper.toDto(createdParticipationRequest);
    }

    @Override
    public List<ParticipationRequestDto> get(Long userId) {
        checkAndGetUserById(userId);

        List<ParticipationRequest> participationRequests = participationRequestRepository.findByRequesterId(userId);
        log.trace("Participation requests are requested by user with id {}", userId);
        return participationRequestMapper.toDto(participationRequests);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        checkAndGetUserById(userId);

        ParticipationRequest participationRequest = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("On part. request cancel - Request doesn't exist with id: " + requestId));

        if (!participationRequest.getRequesterId().equals(userId))
            throw new NotFoundException(String.format("On part. request cancel - " +
                    "Request with id %s can't be canceled by not owner with id %s: ", requestId, userId));

        participationRequest.setStatus(ParticipationRequestStatus.CANCELED);
        participationRequest = participationRequestRepository.save(participationRequest);
        log.info("Participation request is canceled: {}", participationRequest);
        return participationRequestMapper.toDto(participationRequest);
    }

    private UserShortDto checkAndGetUserById(Long userId) {
        try {
            return userClient.getById(userId);
        } catch (FeignException.FeignClientException e) {
            throw new NotFoundException("On part. request operations - User doesn't exist with id: " + userId);
        }
    }
}