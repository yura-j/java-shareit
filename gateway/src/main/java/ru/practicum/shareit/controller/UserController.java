package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.UserClient;
import ru.practicum.shareit.dto.UserDto;
import ru.practicum.shareit.error.AlreadyExistException;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.marker.Update;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.debug("Запрошены пользователи");
        return userClient.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        log.debug("Запрошен пользователь {}", userId);
        return userClient.getUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated(Create.class) @RequestBody UserDto dto) {
        log.debug("Создан пользователь");
        return userClient.createUser(dto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Validated(Update.class) @RequestBody UserDto dto, @PathVariable Long userId) {
        log.debug("Обновлен пользователь {}", userId);
        ResponseEntity<Object> updatedUserDto;
        try {
            updatedUserDto = userClient.updateUser(dto, userId);
        } catch (Exception exception) {
            throw new AlreadyExistException("Такой email уже есть");
        }
        return updatedUserDto;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.debug("Удален пользователь {}", userId);
        userClient.deleteUser(userId);
    }
}
