package ru.practicum.shareit.item.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.error.HasNoAccessException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userStorage;
    private final RequestRepository requestStorage;
    private final ItemRepository itemStorage;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(ItemDto dto, Long ownerId) {
        User owner = userStorage.findById(ownerId).orElseThrow(NotFoundException::new);
        Long requestId = null != dto.getRequestId() ? dto.getRequestId() : 0L;
        ItemRequest request = requestStorage.findById(requestId).orElse(null);
        Item item = ItemMapper.toItem(dto, owner, request);
        itemStorage.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto dto, Long itemId, Long ownerId) {
        Item item = itemStorage.findById(itemId).orElseThrow();
        if (!Objects.equals(item.getOwner().getId(), ownerId)) {
            throw new HasNoAccessException("Нет доступа");
        }

        item.setDescription(null == dto.getDescription() ? item.getDescription() : dto.getDescription());
        item.setName(null == dto.getName() ? item.getName() : dto.getName());
        item.setIsAvailable(null == dto.getAvailable() ? item.getIsAvailable() : dto.getAvailable());
        itemStorage.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemWithBookingsDto getItem(Long itemId, Long ownerId) {
        Item item = itemStorage.findById(itemId).orElseThrow();
        Booking last = bookingRepository.findLastByItemIdAndNotOwnerId(item.getId(), ownerId);
        Booking next = bookingRepository.findNextByItemIdAndNotOwnerId(item.getId(), ownerId);
        return ItemMapper.toItemWithBookings(item, last, next);
    }

    @Override
    public List<ItemWithBookingsDto> getItems(Long ownerId) {
        return itemStorage
                .findItemsByOwnerId(ownerId)
                .stream()
                .map(item -> {
                    Booking last = bookingRepository.findLastByItemIdAndNotOwnerId(item.getId(), ownerId);
                    Booking next = bookingRepository.findNextByItemIdAndNotOwnerId(item.getId(), ownerId);
                    return ItemMapper.toItemWithBookings(item, last, next);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        return itemStorage
                .findAvailableItemsByText(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto postComment(Long ownerId, Long itemId, CreateCommentDto dto) {
        User owner = userStorage.findById(ownerId).orElseThrow(NotFoundException::new);
        Item item = itemStorage.findById(itemId).orElseThrow(NotFoundException::new);
        List<Booking> bookings = bookingRepository
                .findByItemId(itemId)
                .stream()
                .filter(booking -> booking.getStatus() == Status.APPROVED
                        && booking.getStart().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            throw new ValidationException("невозможно оставить отзыв по предмету у которого не было бронирований");
        }

        Comment comment = CommentMapper.fromCreateDto(dto, item, owner);
        commentRepository.save(comment);
        return CommentMapper.fromComment(comment);
    }
}
