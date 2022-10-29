package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.AlreadyExistException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        log.debug("Запрошены пользователи");
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.debug("Запрошен пользователь {}", userId);
        return userService.getUser(userId);
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto dto) {
        log.debug("Создан пользователь");
        return userService.createUser(dto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto dto, @PathVariable Long userId) {
        log.debug("Обновлен пользователь {}", userId);
        UserDto updatedUserDto;
        try {
            updatedUserDto = userService.updateUser(dto, userId);
        } catch (Exception exception) {
            throw new AlreadyExistException("Такой email уже есть");
        }
        return updatedUserDto;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.debug("Удален пользователь {}", userId);
        userService.deleteUser(userId);
    }
}
