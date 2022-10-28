package ru.practicum.shareit.item.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.error.HasNoAccessException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

    private ItemService itemService;
    private UserRepository userRepository;
    private RequestRepository requestRepository;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;

    private ItemDto itemDto;
    private Item item;
    private User owner;
    private Request request;
    private List<Booking> bookings;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        requestRepository = mock(RequestRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);

        itemDto = ItemDto.builder().build();
        owner = new User(1L, "valera", "valera@valera.valera");
        request = new Request(1L, "description", LocalDateTime.now(), owner, List.of());
        bookings = List.of(Booking.builder().build());
        item = new Item(1L,
                "item",
                "description",
                true,
                owner,
                request,
                bookings,
                List.of());

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findLastByItemIdAndNotOwnerId(anyLong(), anyLong()))
                .thenReturn(Booking.builder()
                        .item(item)
                        .booker(owner)
                        .build());
        when(bookingRepository.findNextByItemIdAndNotOwnerId(anyLong(), anyLong()))
                .thenReturn(Booking.builder()
                        .item(item)
                        .booker(owner)
                        .build());

        itemService = new ItemServiceImpl(
                userRepository,
                requestRepository,
                itemRepository,
                bookingRepository,
                commentRepository
        );
    }

    @Test
    void createItem() {
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        itemService.createItem(itemDto, 1L);

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem() {
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        itemService.updateItem(itemDto, 1L, 1L);

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItemCatchNoAccess() {
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        assertThrows(HasNoAccessException.class, () -> {
            itemService.updateItem(itemDto, 1L, 99L);
        });
    }

    @Test
    void getItem() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        itemService.getItem(1L, 1L);

        verify(itemRepository).findById(anyLong());
    }

    @Test
    void getItems() {
        when(itemRepository.findItemsByOwnerId(anyLong()))
                .thenReturn(List.of(item));
        itemService.getItems(1L);
        verify(itemRepository).findItemsByOwnerId(anyLong());
    }

    @Test
    void searchItemsByText() {
        when(itemRepository.findAvailableItemsByText(anyString()))
                .thenReturn(List.of(item));
        itemService.searchItemsByText("search");
        verify(itemRepository).findAvailableItemsByText(anyString());
    }

    @Test
    void postCommentCatchNoBookingsException() {
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(Comment.builder().build());
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        assertThrows(ValidationException.class, () -> {
            itemService.postComment(1L, 1L, new CreateCommentDto());
        });
    }

    @Test
    void postComment() {
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(Comment.builder()
                        .author(owner)
                        .build());
        item.setBookings(List.of(new Booking()));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findByItemId(anyLong()))
                .thenReturn(List.of(new Booking(1L,
                        Status.APPROVED,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        item,
                        owner
                )));
        itemService.postComment(1L, 1L, new CreateCommentDto());
        verify(commentRepository).save(any(Comment.class));
    }
}