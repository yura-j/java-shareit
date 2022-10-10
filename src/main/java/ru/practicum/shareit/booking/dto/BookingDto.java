package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Builder
@Data
public class BookingDto {
    Long id;
    State status;
    User booker;
    Item item;
    LocalDateTime start;
    LocalDateTime end;
}
