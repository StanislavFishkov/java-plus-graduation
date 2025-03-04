package ru.practicum.ewm.common.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.common.util.DateTimeUtil;
import ru.practicum.ewm.common.dto.location.NewLocationDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @Future
    @JsonFormat(pattern = DateTimeUtil.DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    @NotNull
    private NewLocationDto location;

    private Boolean paid;

    @PositiveOrZero(message = "Необходимо указать количество участников")
    private Integer participantLimit;

    private Boolean requestModeration;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}