package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {
    private static final List<Status> REAL_STATE = List.of(Status.APPROVED, Status.REJECTED, Status.WAITING);
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingDto createBooking(BookingCreateDto dto, Long ownerId) {
        Item item = itemRepository.findById(dto.getItemId()).orElseThrow();
        User requester = userRepository.findById(ownerId).orElseThrow();
        boolean isValid =
                item.getIsAvailable()
                        && validateBookingDates(dto.getStart(), dto.getEnd());
        if (!isValid) {
            throw new ValidationException("предмет не доступен для бронирования");
        }

        if (ownerId.equals(item.getOwner().getId())) {
            throw new NotFoundException("не найдено");
        }
        Booking booking = BookingDtoMapper.fromCreateDto(dto, item, requester);
        bookingRepository.save(booking);
        return BookingDtoMapper.fromBooking(booking);
    }

    @Transactional
    public BookingDto approveBooking(Long bookingId, Long ownerId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        User requester = userRepository.findById(ownerId).orElseThrow();
        if (!Objects.equals(booking.getItem().getOwner().getId(), requester.getId())) {
            throw new NotFoundException("только владелец вещи может принять или отклонить заявку на бронирование");
        }
        if (booking.getStatus() == Status.APPROVED) {
            throw new ValidationException("нельзя менять статус после подтверждения");
        }
        Status newStatus = approved ? Status.APPROVED : Status.REJECTED;
        booking.setStatus(newStatus);
        bookingRepository.save(booking);
        return BookingDtoMapper.fromBooking(booking);
    }

    public BookingDto getBooking(Long bookingId, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        User requester = userRepository.findById(ownerId).orElseThrow();
        Long bookerId = booking.getBooker().getId();
        Long itemOwnerId = booking.getItem().getOwner().getId();
        if (!(ownerId.equals(bookerId)
                || ownerId.equals(itemOwnerId))) {
            throw new NotFoundException("нет бронирований");
        }
        return BookingDtoMapper.fromBooking(booking);
    }

    private Boolean validateBookingDates(LocalDateTime start, LocalDateTime end) {
        return end.isAfter(LocalDateTime.now())
                && start.isAfter(LocalDateTime.now())
                && end.isAfter(start);
    }

    public List<BookingDto> getBookings(Long ownerId, Status status, @Min(value = 0) Integer from, @Min(value = 0) Integer size) {
        PageRequest page = PageRequest.of(from/size, size, Sort.by(Sort.Direction.DESC, "start"));
        Page<Booking> bookings = REAL_STATE.contains(status)
                ? bookingRepository.findAllByBookerIdAndStatus(ownerId, status, page)
                : bookingRepository.findAllByBookerId(ownerId, page);
        User requester = userRepository.findById(ownerId).orElseThrow();
        return bookings
                .stream()
                .filter(booking -> filterByState(booking, status))
                .map(BookingDtoMapper::fromBooking)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getOwnerBookings(Long ownerId, Status status, @Min(value = 0) Integer from, @Min(value = 0) Integer size) {
        PageRequest page = PageRequest.of(from/size, size, Sort.by(Sort.Direction.DESC, "start_date"));
        Page<Booking> bookings = REAL_STATE.contains(status)
                ? bookingRepository.findAllByOwnerIdAndStatus(ownerId, status.name(), page)
                : bookingRepository.findAllByOwnerId(ownerId, page);

        User requester = userRepository.findById(ownerId).orElseThrow();
        return bookings
                .stream()
                .filter(booking -> filterByState(booking, status))
                .map(BookingDtoMapper::fromBooking)
                .collect(Collectors.toList());
    }

    private boolean filterByState(Booking booking, Status status) {
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case PAST:
                return booking.getStart().isBefore(now)
                        && booking.getEnd().isBefore(now);
            case CURRENT:
                return booking.getStart().isBefore(now)
                        && booking.getEnd().isAfter(now);
            case FUTURE:
                return booking.getStart().isAfter(now)
                        && booking.getEnd().isAfter(now);
        }
        return true;
    }
}
