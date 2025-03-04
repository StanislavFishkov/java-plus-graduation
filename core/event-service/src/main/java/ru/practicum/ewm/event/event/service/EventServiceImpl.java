package ru.practicum.ewm.event.event.service;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.categories.model.Category;
import ru.practicum.ewm.event.categories.repository.CategoriesRepository;
import ru.practicum.ewm.common.dto.event.*;
import ru.practicum.ewm.common.dto.location.LocationDto;
import ru.practicum.ewm.common.dto.user.UserShortDto;
import ru.practicum.ewm.common.error.exception.ConflictDataException;
import ru.practicum.ewm.common.error.exception.NotFoundException;
import ru.practicum.ewm.common.error.exception.ValidationException;
import ru.practicum.ewm.common.model.event.EventStateActionAdmin;
import ru.practicum.ewm.common.model.event.EventStateActionPrivate;
import ru.practicum.ewm.common.model.event.EventStates;
import ru.practicum.ewm.common.util.DateTimeUtil;
import ru.practicum.ewm.common.util.PagingUtil;
import ru.practicum.ewm.event.event.mapper.EventMapper;
import ru.practicum.ewm.event.event.model.Event;
import ru.practicum.ewm.event.event.model.QEvent;
import ru.practicum.ewm.event.event.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoriesRepository categoriesRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public EventFullDto addEvent(UserShortDto userShortDto, NewEventDto newEventDto, LocationDto locationDto) {
        checkEventTime(newEventDto.getEventDate());

        Category category = categoriesRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("On Event admin add - Event doesn't exist with id: " +
                        newEventDto.getCategory()));

        Event event = eventRepository.save(eventMapper.toEvent(newEventDto, category, userShortDto.getId(), locationDto));
        return eventMapper.toFullDto(event, userShortDto, locationDto);
    }

    @Override
    public List<Event> getEventsByUserId(Long id, int from, int size) {
        PageRequest page = PagingUtil.pageOf(from, size).withSort(Sort.by(Sort.Order.desc("eventDate")));

        return eventRepository.findAllByInitiatorId(id, page);
    }

    @Override
    @Transactional
    public Event updateEvent(Long userId, Long eventId, UpdateEventUserRequestDto eventUpdateDto, LocationDto locationDto) {
        Event event = getEventByIdAndInitiatorId(eventId, userId);

        if (event.getState() == EventStates.PUBLISHED)
            throw new ConflictDataException(
                    String.format("On Event private update - " +
                                    "Event with id %s can't be changed because it is published.", event.getId()));
        checkEventTime(eventUpdateDto.getEventDate());

        eventMapper.update(event, eventUpdateDto, locationDto);
        if (eventUpdateDto.getStateAction() != null) {
            setStateToEvent(eventUpdateDto, event);
        }
        event.setId(eventId);

        event = eventRepository.save(event);
        log.info("Event is updated: {}", event);
        return event;
    }

    @Override
    @Transactional
    public Event update(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto, LocationDto locationDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("On Event admin update - Event doesn't exist with id: " + eventId));

        Category category = null;
        if (updateEventAdminRequestDto.getCategory() != null)
            category = categoriesRepository.findById(updateEventAdminRequestDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("On Event admin update - Category doesn't exist with id: " +
                            updateEventAdminRequestDto.getCategory()));

        event = eventMapper.update(event, updateEventAdminRequestDto, category, locationDto);
        calculateNewEventState(event, updateEventAdminRequestDto.getStateAction());

        event = eventRepository.save(event);
        log.info("Event is updated by admin: {}", event);
        return event;
    }

    @Override
    public Event getById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("On Event public get - Event doesn't exist with id: " + eventId));

    }

    @Override
    public List<Event> get(EventAdminFilterParamsDto filters, int from, int size) {
        QEvent event = QEvent.event;

        BooleanBuilder builder = new BooleanBuilder();

        if (filters.getUsers() != null && !filters.getUsers().isEmpty())
            builder.and(event.initiatorId.in(filters.getUsers()));

        if (filters.getStates() != null && !filters.getStates().isEmpty())
            builder.and(event.state.in(filters.getStates()));

        if (filters.getCategories() != null && !filters.getCategories().isEmpty())
            builder.and(event.category.id.in(filters.getCategories()));

        if (filters.getRangeStart() != null)
            builder.and(event.eventDate.goe(filters.getRangeStart()));

        if (filters.getRangeEnd() != null)
            builder.and(event.eventDate.loe(filters.getRangeEnd()));

        return eventRepository.findAll(builder,
                PagingUtil.pageOf(from, size).withSort(new QSort(event.createdOn.desc()))).toList();
    }

    @Override
    public List<Event> get(EventPublicFilterParamsDto filters, int from, int size, List<Long> locationsIds) {
        QEvent qEvent = QEvent.event;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qEvent.state.eq(EventStates.PUBLISHED));

        if (filters.getText() != null)
            builder.and(qEvent.annotation.containsIgnoreCase(filters.getText())
                    .or(qEvent.description.containsIgnoreCase(filters.getText())));

        if (filters.getCategories() != null && !filters.getCategories().isEmpty())
            builder.and(qEvent.category.id.in(filters.getCategories()));

        if (filters.getPaid() != null)
            builder.and(qEvent.paid.eq(filters.getPaid()));

        if (filters.getRangeStart() == null && filters.getRangeEnd() == null)
            builder.and(qEvent.eventDate.goe(DateTimeUtil.currentDateTime()));
        else {
            if (filters.getRangeStart() != null)
                builder.and(qEvent.eventDate.goe(filters.getRangeStart()));

            if (filters.getRangeEnd() != null)
                builder.and(qEvent.eventDate.loe(filters.getRangeEnd()));
        }

        if (locationsIds != null) {
            builder.and(qEvent.locationId.in(locationsIds));
        }

        PageRequest page = PagingUtil.pageOf(from, size);
        if (filters.getSort() != null && filters.getSort() == EventPublicFilterParamsDto.EventSort.EVENT_DATE)
            page.withSort(new QSort(qEvent.eventDate.desc()));

        return eventRepository.findAll(builder, page).toList();
    }

    @Override
    public boolean existsByLocationId(Long locationId) {
        return eventRepository.existsByLocationId(locationId);
    }

    @Override
    public Event getEventByIdAndInitiatorId(Long eventId, Long initiatorId) {
        return eventRepository.findByIdAndInitiatorId(eventId, initiatorId)
                .orElseThrow(() -> new NotFoundException(String.format("On event operations - " +
                        "Event doesn't exist with id %s or not available for User with id %s: ", eventId, initiatorId)));
    }

    private void calculateNewEventState(Event event, EventStateActionAdmin stateAction) {
        if (stateAction == EventStateActionAdmin.PUBLISH_EVENT) {
            if (event.getState() != EventStates.PENDING) {
                throw new ConflictDataException(
                        String.format("On Event admin update - " +
                                        "Event with id %s can't be published from the state %s: ",
                                event.getId(), event.getState()));
            }

            LocalDateTime currentDateTime = DateTimeUtil.currentDateTime();
            if (currentDateTime.plusHours(1).isAfter(event.getEventDate()))
                throw new ConflictDataException(
                        String.format("On Event admin update - " +
                                        "Event with id %s can't be published because the event date is to close %s: ",
                                event.getId(), event.getEventDate()));

            event.setPublishedOn(currentDateTime);
            event.setState(EventStates.PUBLISHED);
        } else if (stateAction == EventStateActionAdmin.REJECT_EVENT) {
            if (event.getState() == EventStates.PUBLISHED) {
                throw new ConflictDataException(
                        String.format("On Event admin update - " +
                                        "Event with id %s can't be canceled because it is already published: ",
                                event.getState()));
            }

            event.setState(EventStates.CANCELED);
        }
    }

    private void setStateToEvent(UpdateEventUserRequestDto eventUpdateDto, Event event) {
        if (eventUpdateDto.getStateAction().toString()
                .equalsIgnoreCase(EventStateActionPrivate.CANCEL_REVIEW.toString())) {
            event.setState(EventStates.CANCELED);
        } else if (eventUpdateDto.getStateAction().toString()
                .equalsIgnoreCase(EventStateActionPrivate.SEND_TO_REVIEW.toString())) {
            event.setState(EventStates.PENDING);
        }
    }

    private void checkEventTime(LocalDateTime eventDate) {
        if (eventDate == null) return;
        log.info("Проверяем дату события на корректность: {}", eventDate);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime correctEventTime = eventDate.plusHours(2);
        if (correctEventTime.isBefore(now)) {
            log.info("дата не корректна");
            throw new ValidationException("Дата события должна быть +2 часа вперед");
        }
    }
}
