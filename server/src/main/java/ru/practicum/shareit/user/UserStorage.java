package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User save(User user);

    User update(User user);

    Optional<User> findById(Long id);

    List<User> getUsers();

    void delete(Long userId);
}
