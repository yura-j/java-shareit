package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.input.RequestCreateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class RequestServiceTest {

    private RequestService requestService;
    private RequestRepository requestRepository;
    private Request request;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        requestRepository = mock(RequestRepository.class);
        itemRepository = mock(ItemRepository.class);

        requestService = new RequestService(requestRepository,
                userRepository,
                itemRepository);
        request = new Request();
    }

    @Test
    void create() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        when(requestRepository.save(any(Request.class)))
                .thenReturn(request);
        requestService.create(new RequestCreateDto(), 1L);
        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void getMy() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(
                        User.builder()
                                .id(1L)
                                .build()
                ));
        when(requestRepository.findAllByrequester_id(anyLong()))
                .thenReturn(List.of());
        requestService.getMy(1L);
        verify(requestRepository).findAllByrequester_id(anyLong());
    }

    @Test
    void getAllExceptMy() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(
                        User.builder()
                                .id(1L)
                                .build()
                ));
        Page<Request> page = new PageImpl<>(List.of(request));
        when(requestRepository.findAllByrequester_idNot(anyLong(), any(PageRequest.class)))
                .thenReturn(page);
        requestService.getAllExceptMy(1L, 1, 1);
        verify(requestRepository).findAllByrequester_idNot(anyLong(), any(PageRequest.class));
    }

    @Test
    void get() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(User.builder()
                        .id(1L)
                        .build()));
        Optional<Request> delegate = requestRepository.findById(1L);
        when(delegate)
                .thenReturn(Optional.of(request));
        requestService.get(1L, 1L);
        verify(requestRepository).findById(1L);
    }

    @Test
    void getNotNullItems() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(User.builder()
                        .id(1L)
                        .build()));
        request.setItems(List.of(Item.builder().request(request).build()));
        Optional<Request> delegate = requestRepository.findById(1L);
        when(delegate)
                .thenReturn(Optional.of(request));
        requestService.get(1L, 1L);
        verify(requestRepository).findById(1L);
    }
}