package ru.practicum.shareit.item.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.error.HasNoAccessException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.Request;
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
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto dto, Long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(NotFoundException::new);
        Long requestId = null != dto.getRequestId() ? dto.getRequestId() : 0L;
        Request request = requestRepository.findById(requestId).orElse(null);
        Item item = ItemMapper.toItem(dto, owner, request);
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto dto, Long itemId, Long ownerId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        if (!Objects.equals(item.getOwner().getId(), ownerId)) {
            throw new HasNoAccessException("Нет доступа");
        }

        item.setDescription(null == dto.getDescription() ? item.getDescription() : dto.getDescription());
        item.setName(null == dto.getName() ? item.getName() : dto.getName());
        item.setIsAvailable(null == dto.getAvailable() ? item.getIsAvailable() : dto.getAvailable());
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemWithBookingsDto getItem(Long itemId, Long ownerId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        Booking last = bookingRepository.findLastByItemIdAndNotOwnerId(item.getId(), ownerId);
        Booking next = bookingRepository.findNextByItemIdAndNotOwnerId(item.getId(), ownerId);
        return ItemMapper.toItemWithBookings(item, last, next);
    }

    @Override
    public List<ItemWithBookingsDto> getItems(Long ownerId) {
        return itemRepository
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

        return itemRepository
                .findAvailableItemsByText(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto postComment(Long ownerId, Long itemId, CreateCommentDto dto) {
        User owner = userRepository.findById(ownerId).orElseThrow(NotFoundException::new);
        Item item = itemRepository.findById(itemId).orElseThrow(NotFoundException::new);
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
        comment = commentRepository.save(comment);
        return CommentMapper.fromComment(comment);
    }
}
