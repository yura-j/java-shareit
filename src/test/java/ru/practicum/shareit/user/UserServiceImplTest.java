package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.error.AlreadyExistException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UserServiceImplTest {

    private UserRepository userRepository;
    private UserServiceImpl userService;
    private User user;

    @BeforeEach
    void before() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        user = User.builder().build();
    }

    @Test
    void getUsers() {
        when(userRepository.findAll())
                .thenReturn(List.of(user));

        List<UserDto> users = userService.getUsers();

        verify(userRepository).findAll();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(UserDto.class, users.get(0).getClass());
    }

    @Test
    void getUser() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        UserDto user = userService.getUser(1L);

        verify(userRepository).findById(1L);

        assertNotNull(user);
        assertEquals(UserDto.class, user.getClass());
    }

    @Test
    void createUser() {
        User userWithId = User.builder()
                .id(1L)
                .build();
        when(userRepository.save(any(User.class)))
                .thenReturn(userWithId);

        UserDto user = userService.createUser(UserDto.builder().build());

        verify(userRepository).save(any(User.class));

        assertNotNull(user);
        assertEquals(1L, userWithId.getId());
    }

    @Test
    void createDoubleMailUser() {
        User userWithId = User.builder()
                .id(1L)
                .build();
        when(userRepository.save(any(User.class)))
                .thenThrow(new ConstraintViolationException("test", Set.of()));

        assertThrows(AlreadyExistException.class, () -> {
            UserDto user = userService.createUser(UserDto.builder().build());
        });

        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser() {
        User userWithId = User.builder()
                .id(1L)
                .build();
        when(userRepository.save(any(User.class)))
                .thenReturn(userWithId);
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(userWithId));

        UserDto user = userService.updateUser(UserDto.builder().build(), 1L);

        verify(userRepository).save(any(User.class));

        assertNotNull(user);
        assert userWithId != null;
        assertEquals(1L, userWithId.getId());
    }

    @Test
    void partialUpdateNameUser() {
        User userWithId = User.builder()
                .id(1L)
                .build();
        when(userRepository.save(any(User.class)))
                .thenReturn(userWithId);
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(userWithId));

        UserDto user = userService.updateUser(UserDto.builder()
                .name("Vasiliy")
                .build(), 1L);

        verify(userRepository).save(any(User.class));

        assertNotNull(user);
        assert userWithId != null;
        assertEquals(1L, userWithId.getId());
    }

    @Test
    void partialUpdateEmailUser() {
        User userWithId = User.builder()
                .id(1L)
                .build();
        when(userRepository.save(any(User.class)))
                .thenReturn(userWithId);
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(userWithId));

        UserDto user = userService.updateUser(UserDto.builder()
                .email("Vasiliy")
                .build(), 1L);

        verify(userRepository).save(any(User.class));

        assertNotNull(user);
        assert userWithId != null;
        assertEquals(1L, userWithId.getId());
    }

    @Test
    void deleteUser() {
        doNothing().when(userRepository).deleteById(1L);
        userService.deleteUser(1L);
    }
}