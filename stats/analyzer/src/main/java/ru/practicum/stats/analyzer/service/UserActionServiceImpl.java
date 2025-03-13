package ru.practicum.stats.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.analyzer.model.UserAction;
import ru.practicum.stats.analyzer.model.UserActionId;
import ru.practicum.stats.analyzer.model.UserActionType;
import ru.practicum.stats.analyzer.repository.UserActionRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionServiceImpl implements UserActionService {
    private final UserActionRepository userActionRepository;

    @Override
    @Transactional
    public void handleUserAction(UserActionAvro userActionAvro) {
        UserActionId userActionId = new UserActionId(userActionAvro.getEventId(), userActionAvro.getUserId());

        UserAction userAction = userActionRepository.findById(userActionId)
                .orElseGet(() -> UserAction.builder().eventId(userActionId.eventId()).userId(userActionAvro.getUserId()).build());

        UserActionType newUserActionType = UserActionType.valueOf(userActionAvro.getActionType().name());
        if (userAction.getType() != null && userAction.getType().getWeight() > newUserActionType.getWeight()) return;

        userAction.setType(newUserActionType);
        userAction.setWeight(newUserActionType.getWeight());
        userAction.setTimestamp(userActionAvro.getTimestamp());

        userActionRepository.save(userAction);
        log.info("User action has been saved: {}", userAction);
    }
}