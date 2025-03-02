package ru.practicum.ewm.compilation.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.dto.user.UserShortDto;
import ru.practicum.ewm.common.feignclient.UserClient;
import ru.practicum.ewm.common.dto.compilation.CompilationDto;
import ru.practicum.ewm.common.dto.compilation.CompilationRequestDto;
import ru.practicum.ewm.common.dto.compilation.UpdateCompilationRequestDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.common.error.exception.NotFoundException;
import ru.practicum.ewm.common.util.PagingUtil;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    CompilationRepository compilationRepository;
    EventRepository eventRepository;
    CompilationMapper compilationMapper;
    EventMapper eventMapper;
    UserClient userClient;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("getCompilations params: pinned = {}, from = {}, size = {}", pinned, from, size);
        PageRequest page = PagingUtil.pageOf(from, size);

        Page<Compilation> compilations = compilationRepository.findAllByPinned(pinned, page);

        Map<Long, UserShortDto> users = userClient.getByIds(compilations
                        .flatMap(compilation -> compilation.getEvents().stream())
                        .map(Event::getId)
                        .toList())
                .stream()
                .collect(Collectors.toMap(UserShortDto::getId, Function.identity()));

        return compilations
                .map(compilation -> compilationMapper.toDto(compilation, compilation.getEvents()
                        .stream()
                        .map(e -> eventMapper.toShortDto(e, users.get(e.getId())))
                        .toList()))
                .getContent();
    }

    @Override
    public CompilationDto getById(Long compilationId) {
        log.info("getById params: id = {}", compilationId);
        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(() -> new NotFoundException(
                String.format("Подборка с ид %s не найдена", compilationId))
        );

        Map<Long, UserShortDto> users = userClient.getByIds(compilation.getEvents().stream()
                        .map(Event::getId)
                        .toList())
                .stream()
                .collect(Collectors.toMap(UserShortDto::getId, Function.identity()));

        log.info("getById result compilation = {}", compilation);
        return compilationMapper.toDto(compilation, compilation.getEvents().stream()
                .map(e -> eventMapper.toShortDto(e, users.get(e.getId())))
                .toList()
        );
    }

    @Override
    @Transactional
    public CompilationDto addCompilation(CompilationRequestDto compilationRequestDto) {
        log.info("addCompilation params: compilationRequestDto = {}", compilationRequestDto);

        List<Event> events = getAndCheckEventList(compilationRequestDto.getEvents());
        Map<Long, UserShortDto> users = userClient.getByIds(events.stream()
                        .map(Event::getId)
                        .toList())
                .stream()
                .collect(Collectors.toMap(UserShortDto::getId, Function.identity()));

        Compilation compilation = compilationRepository.save(compilationMapper.toEntity(compilationRequestDto, events));
        log.info("addCompilation result compilation = {}", compilation);
        return compilationMapper.toDto(compilation, compilation.getEvents().stream()
                .map(e -> eventMapper.toShortDto(e, users.get(e.getId())))
                .toList()
        );
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequestDto compilationRequestDto) {
        log.info("update params: compilationId = {}, compilationRequestDto = {}", compilationId, compilationRequestDto);
        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(() -> new NotFoundException(
                String.format("Подборка с ид %s не найдена", compilationId))
        );
        List<Event> events = getAndCheckEventList(compilationRequestDto.getEvents());
        Map<Long, UserShortDto> users = userClient.getByIds(events.stream()
                        .map(Event::getId)
                        .toList())
                .stream()
                .collect(Collectors.toMap(UserShortDto::getId, Function.identity()));

        compilationMapper.update(compilationRequestDto, compilationId, events, compilation);
        compilation = compilationRepository.save(compilation);
        log.info("addCompilation result compilation = {}", compilation);

        return compilationMapper.toDto(compilation, compilation.getEvents().stream()
                .map(e -> eventMapper.toShortDto(e, users.get(e.getId())))
                .toList()
        );
    }

    @Override
    @Transactional
    public void delete(Long compilationId) {
        log.info("delete params: compilationId = {}", compilationId);
        compilationRepository.deleteById(compilationId);
    }

    private List<Event> getAndCheckEventList(List<Long> eventIds) {
        log.info("getAndCheckEventList params: eventIds = {}", eventIds);
        if (eventIds == null || eventIds.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<Event> events = eventRepository.findAllById(eventIds);
            log.info("getAndCheckEventList result: events = {}", events);
            if (events.size() != eventIds.size()) {
                throw new NotFoundException("Некорректный список событий");
            }

            return events;
        }
    }
}
