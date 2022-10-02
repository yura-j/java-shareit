package ru.practicum.shareit.request;

import java.util.Optional;

public interface RequestStorage {
    Optional<ItemRequest> findById(Long requestId);
}
