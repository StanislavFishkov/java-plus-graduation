package ru.practicum.ewm.common.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.common.model.participationrequest.ParticipationRequestStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequestDto {
    private List<Long> requestIds;
    private ParticipationRequestStatus status;
}
