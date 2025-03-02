package ru.practicum.ewm.participationrequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.participationrequest.model.ParticipationRequest;
import ru.practicum.ewm.participationrequest.model.ParticipationRequestStatus;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long>,
        QuerydslPredicateExecutor<ParticipationRequest> {
    @Query("Select pr from ParticipationRequest as pr where pr.requesterId = :requesterId and pr.event.initiatorId <> :requesterId")
    List<ParticipationRequest> findByRequesterId(@Param("requesterId") Long requesterId);

    boolean existsByRequesterIdAndEvent(Long requesterId, Event event);

    long countByEventAndStatusIn(Event event, List<ParticipationRequestStatus> status);

    List<ParticipationRequest> findAllByEvent_IdAndStatus(Long eventId, ParticipationRequestStatus status);


}