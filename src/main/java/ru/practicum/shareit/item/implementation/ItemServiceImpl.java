package ru.practicum.shareit.item.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.HasNoAccessException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserStorage userStorage;
    private final RequestStorage requestStorage;
    private final ItemStorage itemStorage;

    @Override
    public ItemDto createItem(ItemDto dto, Long ownerId) {
        User owner = userStorage.findById(ownerId).orElseThrow(NotFoundException::new);
        ItemRequest request = requestStorage.findById(dto.getRequestId()).orElse(null);
        Item item = ItemMapper.toItem(dto, owner, request);
        itemStorage.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto dto, Long itemId, Long ownerId) {
        Item item = itemStorage.findById(itemId).orElseThrow();

        if (!Objects.equals(item.getOwner().getId(), ownerId)) {
            throw new HasNoAccessException("Нет доступа");
        }

        item.setDescription(null == dto.getDescription() ? item.getDescription() : dto.getDescription());
        item.setName(null == dto.getName() ? item.getName() : dto.getName());
        item.setIsAvailable(null == dto.getAvailable() ? item.getIsAvailable() : dto.getAvailable());

        itemStorage.update(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = itemStorage.findById(itemId).orElseThrow();
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItems(Long ownerId) {
        return itemStorage
                .findItemsByOwnerId(ownerId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        return itemStorage
                .findAvailableItemsByText(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
