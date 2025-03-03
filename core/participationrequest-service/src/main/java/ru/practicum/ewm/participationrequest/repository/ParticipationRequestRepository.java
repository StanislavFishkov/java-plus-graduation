package ru.practicum.ewm.participationrequest.repository;

import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.common.dto.participationrequest.ParticipationRequestCountDto;
import ru.practicum.ewm.common.model.participationrequest.ParticipationRequestStatus;
import ru.practicum.ewm.participationrequest.model.ParticipationRequest;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findByRequesterId(Long requesterId);

    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    long countByEventIdAndStatusIn(Long eventId, Collection<ParticipationRequestStatus> status);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByEventIdAndStatus(Long eventId, ParticipationRequestStatus status);

    @Query("Select pr.eventId as eventId, count(pr.id) as confirmedCount " +
            "from ParticipationRequest pr " +
            "where pr.status = ru.practicum.ewm.common.model.participationrequest.ParticipationRequestStatus.CONFIRMED " +
            "and pr.eventId in (:eventIds) group by pr.eventId")
    Stream<Tuple> getConfirmedCountByEventIds(@Param("eventIds") Collection<Long> eventIds);

    @Query("Select new ru.practicum.ewm.common.dto.participationrequest.ParticipationRequestCountDto(pr.eventId, count(pr.id)) " +
            "from ParticipationRequest pr " +
            "where pr.status = ru.practicum.ewm.common.model.participationrequest.ParticipationRequestStatus.CONFIRMED  " +
            "and pr.eventId in (:eventIds) group by pr.eventId")
    List<ParticipationRequestCountDto> getConfirmedCountByEventIds0(@Param("eventIds") Collection<Long> eventIds);
}