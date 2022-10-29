package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item save(Item item);

    Item update(Item item);

    Optional<Item> findById(Long itemId);

    List<Item> findItemsByOwnerId(Long ownerId);

    List<Item> findAvailableItemsByText(String text);
}
