package ru.practicum.shareit.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.Status;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class BookingDto {
    Long id;
    Status status;
    UserDto booker;
    BookingItemDto item;
    LocalDateTime start;
    LocalDateTime end;
}
