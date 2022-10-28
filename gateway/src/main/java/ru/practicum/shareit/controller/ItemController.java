package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.dto.CreateCommentDto;
import ru.practicum.shareit.dto.ItemDto;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.marker.Update;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @Validated({Create.class}) @RequestBody ItemDto dto) {
        log.debug("Создан предмет");
        return itemClient.createItem(dto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @PathVariable Long itemId,
                                             @Validated({Update.class}) @RequestBody ItemDto dto) {
        log.debug("Предмет обновлен {}", itemId);
        return itemClient.updateItem(dto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long itemId) {
        log.debug("Предмет запрошен {}", itemId);
        return itemClient.getItem(itemId, ownerId);
    }

    @GetMapping()
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.debug("Предметы запрошены для пользователя {}", ownerId);
        return itemClient.getItems(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        log.debug("Поиск предметов по подстроке {}", text);
        return itemClient.searchItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                              @PathVariable Long itemId,
                                              @Validated({Create.class}) @RequestBody CreateCommentDto dto) {
        log.debug("Добавление коммента");
        return itemClient.postComment(ownerId, itemId, dto);
    }
}
