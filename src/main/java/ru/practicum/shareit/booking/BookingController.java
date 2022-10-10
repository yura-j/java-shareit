package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.marker.Update;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;
    @PostMapping
    public BookingDto createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                 @Validated({Create.class}) @RequestBody BookingCreateDto dto) {
        log.debug("Зарегистрирован запрос на бронирование вещи " + dto.getItemId());
        return bookingService.createBooking(dto, ownerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @RequestParam Boolean approved,
                              @PathVariable Long bookingId) {
        log.debug("Бронирование подтверждено {}", bookingId);
        return bookingService.approveBooking(bookingId, ownerId, approved);
    }
//
//    @GetMapping("/{itemId}")
//    public ItemDto getItem(@PathVariable Long itemId) {
//        log.debug("Предмет запрошен {}", itemId);
//        return itemService.getItem(itemId);
//    }
//
//    @GetMapping()
//    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
//        log.debug("Предметы запрошены для пользователя {}", ownerId);
//        return itemService.getItems(ownerId);
//    }
}
