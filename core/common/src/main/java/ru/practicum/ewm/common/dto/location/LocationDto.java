package ru.practicum.ewm.common.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    private Long id;
    private double lat;
    private double lon;
    private String name;
    private String address;
}
