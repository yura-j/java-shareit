package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public class BookingDtoMapper {

    public static Booking fromCreateDto(BookingCreateDto dto, Item item, User requester) {
        return Booking
                .builder()
                .booker(requester)
                .item(item)
                .start(dto.getStart())
                .end(dto.getEnd())
                .status(State.WAITING)
                .build();
    }

    public static BookingDto fromBooking(Booking booking) {
        return BookingDto
                .builder()
                .booker(booking.getBooker())
                .item(booking.getItem())
                .status(booking.getStatus())
                .start(booking.getStart())
                .end(booking.getEnd())
                .id(booking.getId())
                .build();
    }
}
