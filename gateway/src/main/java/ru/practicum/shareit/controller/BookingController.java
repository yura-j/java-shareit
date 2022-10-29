package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Status;
import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.dto.BookingCreateDto;
import ru.practicum.shareit.marker.Create;

import javax.validation.ValidationException;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                @Validated({Create.class}) @RequestBody BookingCreateDto dto) {
        log.debug("Зарегистрирован запрос на бронирование вещи");
        return bookingClient.createBooking(dto, ownerId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                @RequestParam Boolean approved,
                                                @PathVariable Long bookingId) {
        log.debug("Бронирование подтверждено {}", bookingId);
        return bookingClient.approveBooking(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @PathVariable Long bookingId) {
        log.debug("Запрошено бронирование {}", bookingId);
        return bookingClient.getBooking(ownerId, bookingId);
    }

    @GetMapping()
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                              @Min(value = 0) @RequestParam(defaultValue = "0", required = false) Integer from,
                                              @Min(value = 0) @RequestParam(defaultValue = "10", required = false) Integer size,
                                              @RequestParam(name = "state", defaultValue = "ALL") String statusParameter) {
        Status status = Status.from(statusParameter);
        if (status == null) {
            throw new IllegalArgumentException("Unknown state: " + statusParameter);
        }
        if (from < 0 || size < 0) {
            throw new ValidationException("Отрицательная пагинация отрицательно сказывается на здоровье запроса");
        }
        log.debug("Бронирования запрошены для пользователя {}", ownerId);
        return bookingClient.getBookings(ownerId, status, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                   @Min(value = 0) @RequestParam(defaultValue = "0", required = false) Integer from,
                                                   @Min(value = 0) @RequestParam(defaultValue = "10", required = false) Integer size,
                                                   @RequestParam(name = "state", defaultValue = "ALL") String statusParameter) {
        Status status = Status.from(statusParameter);
        if (status == null) {
            throw new IllegalArgumentException("Unknown state: " + statusParameter);
        }
        if (from < 0 || size < 0) {
            throw new ValidationException("Отрицательная пагинация отрицательно сказывается на здоровье запроса");
        }
        log.debug("Бронирования владельца запрошены  {}", ownerId);
        return bookingClient.getOwnerBookings(ownerId, status, from, size);
    }
}
