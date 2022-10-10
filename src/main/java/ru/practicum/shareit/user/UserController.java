package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.marker.Update;
import ru.practicum.shareit.user.dto.UserDto;

import javax.transaction.Transactional;
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
    public UserDto createUser(@Validated(Create.class) @RequestBody UserDto dto) {
        log.debug("Создан пользователь");
        return userService.createUser(dto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@Validated(Update.class) @RequestBody UserDto dto, @PathVariable Long userId) {
        log.debug("Обновлен пользователь {}", userId);
        dto.setId(userId);
        return userService.updateUser(dto);
    }

    @DeleteMapping("/{userId}")
    public void updateUser(@PathVariable Long userId) {
        log.debug("Удален пользователь {}", userId);
        userService.deleteUser(userId);
    }
}
