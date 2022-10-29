package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers();

    UserDto getUser(Long userId);

    UserDto createUser(UserDto dto);

    UserDto updateUser(UserDto dto, Long userId);

    void deleteUser(Long userId);
}
