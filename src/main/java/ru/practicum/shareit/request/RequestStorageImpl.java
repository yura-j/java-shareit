package ru.practicum.shareit.request;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RequestStorageImpl implements RequestStorage {
    @Override
    public Optional<ItemRequest> findById(Long requestId) {
        return Optional.empty();
    }
}
