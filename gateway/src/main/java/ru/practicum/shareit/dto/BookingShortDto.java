package ru.practicum.shareit.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookingShortDto {
    Long id;
    Long bookerId;
}
