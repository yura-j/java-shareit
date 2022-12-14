package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @RequestBody ItemDto dto) {
        log.debug("Создан предмет");
        return itemService.createItem(dto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto dto) {
        log.debug("Предмет обновлен {}", itemId);
        return itemService.updateItem(dto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getItem(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long itemId) {
        log.debug("Предмет запрошен {}", itemId);
        return itemService.getItem(itemId, ownerId);
    }

    @GetMapping()
    public List<ItemWithBookingsDto> getItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.debug("Предметы запрошены для пользователя {}", ownerId);
        return itemService.getItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.debug("Поиск предметов по подстроке {}", text);
        return itemService.searchItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                  @PathVariable Long itemId,
                                  @RequestBody CreateCommentDto dto) {
        log.debug("Добавление коммента");
        return itemService.postComment(ownerId, itemId, dto);
    }
}
