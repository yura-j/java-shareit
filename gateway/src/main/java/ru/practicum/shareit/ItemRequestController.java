package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.RequestClient;
import ru.practicum.shareit.dto.input.RequestCreateDto;
import ru.practicum.shareit.marker.Create;

import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(
            @Validated(Create.class) @RequestBody RequestCreateDto dto,
            @RequestHeader("X-Sharer-User-Id") Long ownerId
    ) {
        log.debug("Создана заявка на предмет");
        return requestClient.create(dto, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getMy(
            @RequestHeader("X-Sharer-User-Id") Long ownerId
    ) {
        log.debug("Запрошены предметы");
        return requestClient.getMy(ownerId);
    }

    @GetMapping("all")
    public ResponseEntity<Object> getAllExceptMy(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @Validated @PositiveOrZero @RequestParam(defaultValue = "0", required = false) Integer from,
            @Validated @Positive @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        log.debug("Запрошены предметы");
        if (from < 0 || size < 0) {
            throw new ValidationException("Отрицательная пагинация отрицательно сказывается на здоровье запроса");
        }
        return requestClient.getAllExceptMy(ownerId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> get(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long requestId
    ) {
        log.debug("Запрошены предметы");
        return requestClient.get(requestId, ownerId);
    }

}
