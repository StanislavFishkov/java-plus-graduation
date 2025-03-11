package ru.practicum.stats.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.stats.analyzer.model.UserAction;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
}