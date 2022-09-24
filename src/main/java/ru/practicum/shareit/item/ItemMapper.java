package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        Long requestId = item.getRequest() != null ? item.getRequest().getId() : null;
        Long ownerId = item.getOwner() != null ? item.getOwner().getId() : null;
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .requestId(requestId)
                .ownerId(ownerId)
                .build();
    }

    public static Item toItem(ItemDto dto, User owner, ItemRequest request) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .isAvailable(dto.getAvailable())
                .request(request)
                .owner(owner)
                .build();
    }
}
