package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.AlreadyExistException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userStorage;

    @Override
    public List<UserDto> getUsers() {
        return userStorage
                .findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = userStorage.findById(userId).orElseThrow();
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto dto) {
        User user = UserMapper.toUser(dto);
        try {
            User savedUser = userStorage.save(user);
            return UserMapper.toUserDto(savedUser);
        } catch (Exception exception) {
            throw new AlreadyExistException("Такой email уже есть");
        }
    }

    @Override
    public UserDto updateUser(UserDto dto) {
        User user = userStorage.findById(dto.getId()).orElseThrow();

        user.setName(null == dto.getName() ? user.getName() : dto.getName());
        user.setEmail(null == dto.getEmail() ? user.getEmail() : dto.getEmail());

        try {
            userStorage.save(user);
        } catch (Exception exception) {
            throw new AlreadyExistException("Такой email уже есть");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteById(userId);
    }
}