package ru.practicum.stats.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserActionServiceImpl implements UserActionService {
    @Override
    public void handleUserAction(UserActionAvro userActionAvro) {

        log.info("User action has been handled: {}", userActionAvro);
    }
}