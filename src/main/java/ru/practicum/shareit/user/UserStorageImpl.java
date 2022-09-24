package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserStorageImpl implements UserStorage {

    private final Map<Long, User> storage = new HashMap<>();
    private Long index = 1L;

    @Override
    public User save(User user) {
        user.setId(index);
        storage.put(index++, user);
        return user;
    }

    @Override
    public User update(User user) {
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long userId) {
        if (!storage.containsKey(userId)) {
            return Optional.empty();
        }
        return Optional.of(storage.get(userId));
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(Long userId) {
        storage.remove(userId);
    }
}
