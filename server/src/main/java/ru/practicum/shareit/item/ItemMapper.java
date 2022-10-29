package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.implementation.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.dto.output.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        Long requestId = item.getRequest() != null ? item.getRequest().getId() : null;
        Long ownerId = item.getOwner() != null ? item.getOwner().getId() : null;
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .requestId(requestId)
                .ownerId(ownerId)
                .build();
    }

    public static Item toItem(ItemDto dto, User owner, Request request) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .isAvailable(dto.getAvailable())
                .request(request)
                .owner(owner)
                .build();
    }

    public static ItemWithBookingsDto toItemWithBookings(Item item, Booking last, Booking next) {
        Long requestId = item.getRequest() != null ? item.getRequest().getId() : null;
        Long ownerId = item.getOwner() != null ? item.getOwner().getId() : null;
        return ItemWithBookingsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .requestId(requestId)
                .ownerId(ownerId)
                .lastBooking(last == null ? null : BookingDtoMapper.shortFromBooking(last))
                .nextBooking(next == null ? null : BookingDtoMapper.shortFromBooking(next))
                .comments(item
                        .getComments()
                        .stream()
                        .map(CommentMapper::fromComment)
                        .collect(Collectors.toList())
                )
                .build();
    }

    public static BookingItemDto toBookingItemDto(Item item) {
        return BookingItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .build();
    }

    public static ItemRequestDto toItemRequestDto(Item item) {
        return ItemRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .requestId(item.getRequest().getId())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .build();
    }
}
