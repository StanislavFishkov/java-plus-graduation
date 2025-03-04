package ru.practicum.ewm.participationrequest.service;

import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.dto.event.EventFullDto;
import ru.practicum.ewm.common.dto.event.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.common.dto.event.EventRequestStatusUpdateResultDto;
import ru.practicum.ewm.common.dto.participationrequest.ParticipationRequestDto;
import ru.practicum.ewm.common.error.exception.ConflictDataException;
import ru.practicum.ewm.common.error.exception.NotFoundException;
import ru.practicum.ewm.common.model.event.EventStates;
import ru.practicum.ewm.common.model.participationrequest.ParticipationRequestStatus;
import ru.practicum.ewm.participationrequest.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.participationrequest.model.ParticipationRequest;
import ru.practicum.ewm.participationrequest.repository.ParticipationRequestRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestMapper participationRequestMapper;

    @Override
    @Transactional
    public ParticipationRequestDto create(Long userId, EventFullDto event) {
        final Long eventId = event.getId();

        if (!event.getState().equals(EventStates.PUBLISHED))
            throw new ConflictDataException("On part. request create - " +
                    "Event isn't published with id: " + eventId);

        if (event.getInitiator().getId().equals(userId))
            throw new ConflictDataException(
                    String.format("On part. request create - " +
                            "Event with id %s has Requester with id %s as an initiator: ", eventId, userId));

        if (participationRequestRepository.existsByRequesterIdAndEventId(userId, eventId))
            throw new ConflictDataException(
                    String.format("On part. request create - " +
                            "Request by Requester with id %s and Event with id %s already exists: ", eventId, userId));

        if (event.getParticipantLimit() != 0) {
            long requestsCount = participationRequestRepository.countByEventIdAndStatusIn(eventId,
                    List.of(ParticipationRequestStatus.CONFIRMED));
            if (requestsCount >= event.getParticipantLimit())
                throw new ConflictDataException(
                        String.format("On part. request create - " +
                                "Event with id %s reached the limit of participants and User with id %s can't apply: ", eventId, userId));
        }

        ParticipationRequest createdParticipationRequest = participationRequestRepository.save(
                ParticipationRequest.builder()
                        .requesterId(userId)
                        .eventId(eventId)
                        .status(event.getParticipantLimit() != 0 && event.getRequestModeration() ?
                                ParticipationRequestStatus.PENDING : ParticipationRequestStatus.CONFIRMED)
                        .build()
        );
        log.info("Participation request is created: {}", createdParticipationRequest);
        return participationRequestMapper.toDto(createdParticipationRequest);
    }

    @Override
    public List<ParticipationRequestDto> get(Long userId) {
        List<ParticipationRequest> participationRequests = participationRequestRepository.findByRequesterId(userId);
        log.trace("Participation requests are requested by user with id {}", userId);
        return participationRequestMapper.toDto(participationRequests);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
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

    @Override
    public ParticipationRequestDto getById(Long requestId) {
        ParticipationRequest participationRequest = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("On part. request getById - Request doesn't exist with id: " + requestId));
        log.trace("Participation request is requested by id: {}", requestId);
        return participationRequestMapper.toDto(participationRequest);
    }

    @Override
    public List<ParticipationRequestDto> getByIds(List<Long> requestIds) {
        List<ParticipationRequest> participationRequests = participationRequestRepository.findAllById(requestIds);
        log.trace("Participation request is requested by ids: {}", requestIds);
        return participationRequestMapper.toDto(participationRequests);
    }

    @Override
    public List<ParticipationRequestDto> getByEventId(Long eventId, ParticipationRequestStatus status) {
        List<ParticipationRequest> participationRequests;
        if (status == null) {
            participationRequests = participationRequestRepository.findAllByEventId(eventId);
            log.trace("Participation requests are requested by eventId {}", eventId);
        } else {
            participationRequests = participationRequestRepository.findAllByEventIdAndStatus(eventId, status);
            log.trace("Participation requests are requested by eventId {} and status {}", eventId, status);
        }
        return participationRequestMapper.toDto(participationRequests);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResultDto updateEventRequestsStatus(Long eventId, Integer participantsLimit,
                                                                       EventRequestStatusUpdateRequestDto statusUpdateRequest) {
        List<ParticipationRequest> requestToChangeStatus = participationRequestRepository.findAllById(statusUpdateRequest.getRequestIds());

        if (statusUpdateRequest.getRequestIds().size() > requestToChangeStatus.size()) {
            Map<Long, ParticipationRequest> foundRequests = requestToChangeStatus.stream()
                    .collect(Collectors.toMap(ParticipationRequest::getId, Function.identity()));
            List<Long> notFoundIds = statusUpdateRequest.getRequestIds().stream()
                    .filter(id -> !foundRequests.containsKey(id))
                    .toList();
            throw new NotFoundException("Participation requests are not found to change status: %s".formatted(notFoundIds));
        }

        log.info("Заявки:  Лимит: {}, а заявок {}, разница между ними: {}", participantsLimit,
                statusUpdateRequest.getRequestIds().size(), (participantsLimit
                        - statusUpdateRequest.getRequestIds().size()));

        long confirmedRequestsCount = participationRequestRepository.countByEventIdAndStatusIn(eventId,
                List.of(ParticipationRequestStatus.CONFIRMED));

        if (statusUpdateRequest.getStatus().equals(ParticipationRequestStatus.CONFIRMED)) {
            log.info("меняем статус заявок для статуса: {}", ParticipationRequestStatus.CONFIRMED);
            if ((participantsLimit - (confirmedRequestsCount) - statusUpdateRequest.getRequestIds().size()) >= 0) {
                for (ParticipationRequest request : requestToChangeStatus) {
                    request.setStatus(ParticipationRequestStatus.CONFIRMED);
                    participationRequestRepository.save(request);
                }
                return new EventRequestStatusUpdateResultDto(requestToChangeStatus
                        .stream().map(participationRequestMapper::toDto)
                        .toList(), null);
            } else {
                throw new ConflictDataException("слишком много участников. Лимит: " + participantsLimit +
                        ", уже подтвержденных заявок: " + confirmedRequestsCount + ", а заявок на одобрение: " +
                        statusUpdateRequest.getRequestIds().size() +
                        ". Разница между ними: " + (participantsLimit - confirmedRequestsCount -
                        statusUpdateRequest.getRequestIds().size()));
            }
        } else if (statusUpdateRequest.getStatus().equals(ParticipationRequestStatus.REJECTED)) {
            log.info("меняем статус заявок для статуса: {}", ParticipationRequestStatus.REJECTED);
            for (ParticipationRequest request : requestToChangeStatus) {
                if (request.getStatus() == ParticipationRequestStatus.CONFIRMED) {
                    throw new ConflictDataException("Заявка" + request.getStatus() + "уже подтверждена.");
                }
                request.setStatus(ParticipationRequestStatus.REJECTED);
                participationRequestRepository.save(request);
            }
            return new EventRequestStatusUpdateResultDto(null, requestToChangeStatus
                    .stream().map(participationRequestMapper::toDto)
                    .toList());
        }
        return null;
    }

    @Override
    public Map<Long, Long> getConfirmedCountByEventIds(List<Long> eventIds) {
        Stream<Tuple> result = participationRequestRepository.getConfirmedCountByEventIds(eventIds);
        log.trace("Confirmed participation request counts are requested by event ids: {}", eventIds);
        return result.collect(
                Collectors.toMap(p -> p.get("eventId", Long.class), p -> p.get("confirmedCount", Long.class)));
    }
}