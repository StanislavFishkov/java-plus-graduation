package ru.practicum.ewm.event.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.common.dto.event.*;
import ru.practicum.ewm.common.dto.location.LocationDto;
import ru.practicum.ewm.common.dto.location.NewLocationDto;
import ru.practicum.ewm.common.dto.participationrequest.ParticipationRequestDto;
import ru.practicum.ewm.common.dto.user.UserShortDto;
import ru.practicum.ewm.common.error.exception.NotFoundException;
import ru.practicum.ewm.common.error.exception.ValidationException;
import ru.practicum.ewm.common.feignclient.LocationClient;
import ru.practicum.ewm.common.feignclient.ParticipationRequestClient;
import ru.practicum.ewm.common.feignclient.UserClient;
import ru.practicum.ewm.common.model.event.EventStates;
import ru.practicum.ewm.common.model.participationrequest.ParticipationRequestStatus;
import ru.practicum.ewm.event.event.mapper.EventMapper;
import ru.practicum.ewm.event.event.model.Event;
import ru.practicum.grpc.stats.action.ActionTypeProto;
import ru.practicum.stats.client.StatClientAnalyzer;
import ru.practicum.stats.client.StatClientCollector;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class EventFacadeImpl implements EventFacade {
    private final EventService eventService;
    private final EventMapper eventMapper;
    private final UserClient userClient;
    private final LocationClient locationClient;
    private final ParticipationRequestClient participationRequestClient;
    private final StatClientCollector statClientCollector;
    private final StatClientAnalyzer statClientAnalyzer;

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        UserShortDto userShortDto = userClient.getById(userId);
        LocationDto locationDto = getOrCreateLocation(newEventDto.getLocation());

        return eventService.addEvent(userShortDto, newEventDto, locationDto);
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Long id, int from, int size) {
        UserShortDto userShortDto = userClient.getById(id);

        List<Event> events = eventService.getEventsByUserId(id, from, size);

        List<EventShortDto> eventsDto = events.stream()
                .map(event -> eventMapper.toShortDto(event, userShortDto))
                .toList();

        populateWithConfirmedRequests(events, eventsDto);
        populateWithStats(eventsDto);

        return eventsDto;
    }

    @Override
    public EventFullDto getEventById(Long userId, Long eventId) {
        UserShortDto userShortDto = userClient.getById(userId);
        Event event = eventService.getEventByIdAndInitiatorId(eventId, userId);
        LocationDto locationDto = locationClient.getById(event.getLocationId());

        EventFullDto eventDto = eventMapper.toFullDto(event, userShortDto, locationDto);
        populateWithConfirmedRequests(List.of(event), List.of(eventDto));
        populateWithStats(List.of(eventDto));

        return eventDto;
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequestDto eventUpdateDto) {
        UserShortDto userShortDto = userClient.getById(userId);
        LocationDto locationDto = getOrCreateLocation(eventUpdateDto.getLocation());

        Event event = eventService.updateEvent(userId, eventId, eventUpdateDto, locationDto);

        EventFullDto eventDto = eventMapper.toFullDto(event, userShortDto, locationDto);
        populateWithConfirmedRequests(List.of(event), List.of(eventDto));
        populateWithStats(List.of(eventDto));

        return eventDto;
    }

    @Override
    public EventFullDto update(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto) {
        LocationDto locationDto = getOrCreateLocation(updateEventAdminRequestDto.getLocation());

        Event event = eventService.update(eventId, updateEventAdminRequestDto, locationDto);

        UserShortDto userShortDto = userClient.getById(event.getInitiatorId());

        EventFullDto eventDto = eventMapper.toFullDto(event, userShortDto, locationDto);
        populateWithConfirmedRequests(List.of(event), List.of(eventDto));
        populateWithStats(List.of(eventDto));

        return eventDto;
    }

    @Override
    public EventFullDto get(Long eventId, Long userId) {
        Event event = eventService.getById(eventId);

        if (event.getState() != EventStates.PUBLISHED)
            throw new NotFoundException("On Event public get - Event isn't published with id: " + eventId);

        UserShortDto userShortDto = userClient.getById(event.getInitiatorId());
        LocationDto locationDto = locationClient.getById(event.getLocationId());

        EventFullDto eventDto = eventMapper.toFullDto(event, userShortDto, locationDto);
        populateWithConfirmedRequests(List.of(event), List.of(eventDto));
        populateWithStats(List.of(eventDto));

        statClientCollector.collectUserAction(eventId, userId, ActionTypeProto.ACTION_VIEW, Instant.now());

        return eventDto;
    }

    @Override
    public EventFullDto getById(Long eventId) {
        Event event = eventService.getById(eventId);

        UserShortDto userShortDto = userClient.getById(event.getInitiatorId());
        LocationDto locationDto = locationClient.getById(event.getLocationId());

        return eventMapper.toFullDto(event, userShortDto, locationDto);
    }

    @Override
    public List<EventFullDto> get(EventAdminFilterParamsDto filters, int from, int size) {
        List<Event> events = eventService.get(filters, from, size);

        Map<Long, UserShortDto> users = userClient.getByIds(events.stream().map(Event::getInitiatorId).toList()).stream()
                .collect(Collectors.toMap(UserShortDto::getId, Function.identity()));
        Map<Long, LocationDto> locations = locationClient.getByIds(events.stream().map(Event::getLocationId).toList()).stream()
                .collect(Collectors.toMap(LocationDto::getId, Function.identity()));

        List<EventFullDto> eventsDto = events.stream()
                .map(e -> eventMapper.toFullDto(e, users.get(e.getInitiatorId()), locations.get(e.getLocationId())))
                .toList();
        populateWithConfirmedRequests(events, eventsDto);
        populateWithStats(eventsDto);

        return eventsDto;
    }

    @Override
    public List<EventShortDto> get(EventPublicFilterParamsDto filters, int from, int size, HttpServletRequest request) {
        List<Long> locationsIds = null;
        if (filters.getLon() != null && filters.getLat() != null) {
            locationsIds = locationClient.getByCoordinatesAndRadius(filters.getLat(), filters.getLon(),
                            filters.getRadius()).stream()
                    .map(LocationDto::getId)
                    .toList();
        }

        List<Event> events = eventService.get(filters, from, size, locationsIds);

        Map<Long, UserShortDto> users = userClient.getByIds(events.stream().map(Event::getInitiatorId).toList()).stream()
                .collect(Collectors.toMap(UserShortDto::getId, Function.identity()));

        List<EventShortDto> eventsDto = events.stream()
                .map(e -> eventMapper.toShortDto(e, users.get(e.getInitiatorId())))
                .collect(Collectors.toCollection(ArrayList::new));
        populateWithConfirmedRequests(events, eventsDto, true);
        populateWithStats(eventsDto);

        if (filters.getSort() != null && filters.getSort() == EventPublicFilterParamsDto.EventSort.RATING)
            eventsDto.sort(Comparator.comparing(EventShortDto::getRating).reversed());

        return eventsDto;
    }

    @Override
    public List<ParticipationRequestDto> getEventAllParticipationRequests(Long userId, Long eventId) {
        Event event = eventService.getById(eventId);

        checkEventOwner(event, userId);
        return participationRequestClient.getByEventId(eventId, ParticipationRequestStatus.PENDING);
    }

    @Override
    public EventRequestStatusUpdateResultDto changeEventState(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequestDto statusUpdateRequest) {
        log.info("Change event state by user: {} and event id: {}", userId, eventId);

        Event event = eventService.getById(eventId);

        userClient.getById(userId);
        checkEventOwner(event, userId);
        int participantsLimit = event.getParticipantLimit();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            log.info("Заявки подтверждать не требуется");
            return null;
        }

        return participationRequestClient.updateEventRequestsStatus(eventId, participantsLimit, statusUpdateRequest);
    }

    @Override
    public boolean existsByLocationId(Long locationId) {
        return eventService.existsByLocationId(locationId);
    }

    @Override
    public List<RecommendedEventDto> getRecommendations(Long userId, int maxResults) {
        List<RecommendedEventDto> recommendations = statClientAnalyzer.getRecommendationsForUser(userId, maxResults)
                .map(e -> new RecommendedEventDto(e.getEventId(), e.getScore()))
                .toList();

        log.info("Recommendations for user {} are retrieved (maxResults - {}): {}", userId, maxResults, recommendations);
        return recommendations;
    }

    @Override
    public void like(Long userId, Long eventId) {
        participationRequestClient.getByEventId(eventId, ParticipationRequestStatus.CONFIRMED).stream()
                .filter(pr -> pr.getRequester().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("It is prohibited to like events without confirmed registration"));

        statClientCollector.collectUserAction(userId, eventId, ActionTypeProto.ACTION_LIKE, Instant.now());
        log.info("User {} liked event {}", userId, eventId);
    }

    private LocationDto getOrCreateLocation(NewLocationDto newLocationDto) {
        return newLocationDto == null ? null : locationClient.createLocationByCoordinates(newLocationDto.getLat(), newLocationDto.getLon());
    }

    private void checkEventOwner(Event event, Long userId) {
        if (!Objects.equals(event.getInitiatorId(), userId)) {
            throw new ValidationException("Событие создал другой пользователь");
        }
    }

    private void populateWithConfirmedRequests(List<Event> events, List<? extends EventShortDto> eventsDto) {
        populateWithConfirmedRequests(events, eventsDto, null);
    }

    private void populateWithConfirmedRequests(List<Event> events, List<?  extends EventShortDto>  eventsDto, Boolean filterOnlyAvailable) {
        Map<Long, Long> confirmedRequests = participationRequestClient.getConfirmedCountByEventIds(
                events.stream()
                        .map(Event::getId)
                        .toList()
        );

        eventsDto
                .forEach(event -> event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0L)));

        if (filterOnlyAvailable != null && filterOnlyAvailable) {
            final Map<Long, Integer> limits = events.stream().collect(Collectors.toMap(Event::getId, Event::getParticipantLimit));
            eventsDto.removeIf(event -> {
                int limit = limits.getOrDefault(event.getId(), -1);
                return limit == 0 || limit <= event.getConfirmedRequests();
            });
        }
    }

    private void populateWithStats(List<? extends EventShortDto> eventsDto) {
        if (eventsDto.isEmpty()) return;

        Map<Long, EventShortDto> events = eventsDto.stream()
                .collect(Collectors.toMap(EventShortDto::getId, Function.identity()));

        statClientAnalyzer.getInteractionsCount(events.keySet().stream().toList())
                        .forEach(stat -> Optional.ofNullable(events.get(stat.getEventId()))
                                .ifPresent(e -> e.setRating(stat.getScore())));
    }
}