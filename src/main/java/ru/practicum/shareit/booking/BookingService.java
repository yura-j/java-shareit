package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.error.HasNoAccessException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingDto createBooking(BookingCreateDto dto, Long ownerId) {
        Item item = itemRepository.findById(dto.getItemId()).orElseThrow();
        User requester = userRepository.findById(ownerId).orElseThrow();
        Booking booking = BookingDtoMapper.fromCreateDto(dto, item, requester);
        bookingRepository.save(booking);
        return BookingDtoMapper.fromBooking(booking);
    }

    public BookingDto approveBooking(Long bookingId, Long ownerId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        User requester = userRepository.findById(ownerId).orElseThrow();
        if (!Objects.equals(booking.getItem().getOwner().getId(), requester.getId())) {
            throw new HasNoAccessException("только владелец вещи может принять или отклонить заявку на бронирование");
        }
        State newState = approved ? State.APPROVED : State.REJECTED;
        booking.setStatus(newState);
        bookingRepository.save(booking);
        return BookingDtoMapper.fromBooking(booking);
    }
}
