package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.marker.Update;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @Validated({Create.class}) @RequestBody ItemDto dto) {
        log.debug("Создан предмет");
        return itemService.createItem(dto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @PathVariable Long itemId,
                              @Validated({Update.class}) @RequestBody ItemDto dto) {
        log.debug("Предмет обновлен {}", itemId);
        return itemService.updateItem(dto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.debug("Предмет запрошен {}", itemId);
        return itemService.getItem(itemId);
    }

    @GetMapping()
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.debug("Предметы запрошены для пользователя {}", ownerId);
        return itemService.getItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.debug("Поиск предметов по подстроке {}", text);
        return itemService.searchItemsByText(text);
    }
}
