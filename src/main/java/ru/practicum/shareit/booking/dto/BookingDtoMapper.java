package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

public interface BookingDtoMapper {

    static Booking fromCreateDto(BookingCreateDto dto, Item item, User requester) {
        return Booking
                .builder()
                .booker(requester)
                .item(item)
                .start(dto.getStart())
                .end(dto.getEnd())
                .status(Status.WAITING)
                .build();
    }

    static BookingDto fromBooking(Booking booking) {
        return BookingDto
                .builder()
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .item(ItemMapper.toBookingItemDto(booking.getItem()))
                .status(booking.getStatus())
                .start(booking.getStart())
                .end(booking.getEnd())
                .id(booking.getId())
                .build();
    }

    static BookingShortDto shortFromBooking(Booking booking) {
        return BookingShortDto
                .builder()
                .bookerId(booking.getBooker().getId())
                .id(booking.getId())
                .build();
    }
}
