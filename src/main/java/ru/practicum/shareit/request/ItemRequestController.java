package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.request.dto.input.RequestCreateDto;
import ru.practicum.shareit.request.dto.output.RequestDto;

import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {

    private final RequestService requestService;

    @PostMapping
    public RequestDto createRequest(
            @Validated(Create.class) @RequestBody RequestCreateDto dto,
            @RequestHeader("X-Sharer-User-Id") Long ownerId
    ) {
        log.debug("Создана заявка на предмет");
        return requestService.create(dto, ownerId);
    }

    @GetMapping
    public List<RequestDto> getMy(
            @RequestHeader("X-Sharer-User-Id") Long ownerId
    ) {
        log.debug("Запрошены предметы");
        return requestService.getMy(ownerId);
    }

    @GetMapping("all")
    public List<RequestDto> getAllExceptMy(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @Min(value = 0) @RequestParam(defaultValue = "0", required = false) Integer from,
            @Min(value = 0) @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        log.debug("Запрошены предметы");
        if (from < 0 || size < 0) {
            throw new ValidationException("Отрицательная пагинация отрицательно сказывается на здоровье запроса");
        }
        return requestService.getAllExceptMy(ownerId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestDto get(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long requestId
    ) {
        log.debug("Запрошены предметы");
        return requestService.get(requestId, ownerId);
    }

}
