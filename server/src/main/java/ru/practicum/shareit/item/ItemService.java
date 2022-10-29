package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto dto, Long ownerId);

    ItemDto updateItem(ItemDto dto, Long itemId, Long ownerId);

    ItemWithBookingsDto getItem(Long itemId, Long ownerId);

    List<ItemWithBookingsDto> getItems(Long ownerId);

    List<ItemDto> searchItemsByText(String text);

    CommentDto postComment(Long ownerId, Long itemId, CreateCommentDto dto);
}
