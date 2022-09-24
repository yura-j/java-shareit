package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto dto, Long ownerId);

    ItemDto updateItem(ItemDto dto, Long itemId, Long ownerId);

    ItemDto getItem(Long itemId);

    List<ItemDto> getItems(Long ownerId);

    List<ItemDto> searchItemsByText(String text);
}
