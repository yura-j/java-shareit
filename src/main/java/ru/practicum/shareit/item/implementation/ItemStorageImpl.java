package ru.practicum.shareit.item.implementation;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImpl implements ItemStorage {

    private final Map<Long, Item> storage = new HashMap<>();
    private Long index = 1L;

    @Override
    public Item save(Item item) {
        item.setId(index);
        storage.put(index++, item);
        return item;
    }

    @Override
    public Item update(Item item) {
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        if (!storage.containsKey(itemId)) {
            return Optional.empty();
        }
        return Optional.of(storage.get(itemId));
    }

    @Override
    public List<Item> findItemsByOwnerId(Long ownerId) {
        return storage
                .values()
                .stream()
                .filter(item -> Objects.equals(ownerId, item.getOwner().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findAvailableItemsByText(String text) {
        return storage
                .values()
                .stream()
                .filter(item -> containsText(item, text) && item.getIsAvailable())
                .collect(Collectors.toList());
    }

    private Boolean containsText(Item item, String text) {
        return !text.isBlank()
                && (item.getDescription().toLowerCase().contains(text.toLowerCase())
                || item.getName().toLowerCase().contains(text.toLowerCase()));
    }
}
