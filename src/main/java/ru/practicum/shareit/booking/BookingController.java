package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.marker.Create;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                    @Validated({Create.class}) @RequestBody BookingCreateDto dto) {
        log.debug("Зарегистрирован запрос на бронирование вещи");
        return bookingService.createBooking(dto, ownerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                    @RequestParam Boolean approved,
                                    @PathVariable Long bookingId) {
        log.debug("Бронирование подтверждено {}", bookingId);
        return bookingService.approveBooking(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                 @PathVariable Long bookingId) {
        log.debug("Запрошено бронирование {}", bookingId);
        return bookingService.getBooking(bookingId, ownerId);
    }

    @GetMapping()
    public List<BookingDto> getBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                        @RequestParam(name = "state", defaultValue = "ALL") String statusParameter) {
        Status status = Status.from(statusParameter);
        if (status == null) {
            throw new IllegalArgumentException("Unknown state: " + statusParameter);
        }
        log.debug("Бронирования запрошены для пользователя {}", ownerId);
        return bookingService.getBookings(ownerId, status);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @RequestParam(name = "state", defaultValue = "ALL") String statusParameter) {
        Status status = Status.from(statusParameter);
        if (status == null) {
            throw new IllegalArgumentException("Unknown state: " + statusParameter);
        }
        log.debug("Бронирования владельца запрошены  {}", ownerId);
        return bookingService.getOwnerBookings(ownerId, status);
    }
}
