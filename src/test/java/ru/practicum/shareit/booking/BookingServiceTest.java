package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    private UserRepository userRepository;
    private RequestRepository requestRepository;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private BookingService bookingService;
    private User requester;
    private User owner;
    private Request request;
    private Booking booking;
    private List<Booking> bookings;
    private Item item;
    private User sideUser;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);

        bookingService = new BookingService(
                bookingRepository,
                itemRepository,
                userRepository);

        owner = new User(1L, "valera", "valera@valera.valera");
        requester = new User(2L, "valera", "valera@valera.valera");
        sideUser = new User(3L, "valera", "valera@valera.valera");
        request = new Request(1L, "description", LocalDateTime.now(), owner, List.of());
        booking = new Booking(1L, Status.APPROVED, LocalDateTime.now(), LocalDateTime.now(), null, requester);
        bookings = List.of(booking);
        item = new Item(1L,
                "item",
                "description",
                true,
                owner,
                request,
                bookings,
                List.of());
        booking.setItem(item);
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(owner));
        when(userRepository.findById(2L))
                .thenReturn(Optional.ofNullable(requester));
        when(userRepository.findById(3L))
                .thenReturn(Optional.ofNullable(sideUser));
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
    }

    @Test
    void createBooking() {
        BookingCreateDto createDto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingService.createBooking(createDto, 2L);

        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBookingItemNotAvailableCatchValidationException() {
        item.setIsAvailable(false);
        BookingCreateDto createDto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(createDto, 2L);
        });

    }

    @Test
    void createBookingDateEndInPastCatchValidationException() {

        BookingCreateDto createDto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .build();
        assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(createDto, 2L);
        });

    }

    @Test
    void createBookingDateStartInPastCatchValidationException() {

        BookingCreateDto createDto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(createDto, 2L);
        });

    }

    @Test
    void createBookingDateInvalidCatchValidationException() {

        BookingCreateDto createDto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();
        assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(createDto, 2L);
        });
    }

    @Test
    void createBookingSelfRequestingCatchNotFoundException() {
        BookingCreateDto createDto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(createDto, 1L);
        });
    }

    @Test
    void approveBooking() {
        booking.setStatus(Status.WAITING);
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto resultDto = bookingService.approveBooking(1L, 1L, true);

        verify(bookingRepository).save(any(Booking.class));

        assertEquals(Status.APPROVED, resultDto.getStatus());
    }

    @Test
    void approveBookingRejected() {
        booking.setStatus(Status.WAITING);
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto resultDto = bookingService.approveBooking(1L, 1L, false);

        verify(bookingRepository).save(any(Booking.class));

        assertEquals(Status.REJECTED, resultDto.getStatus());
    }

    @Test
    void approveBookingNotOwnerCatchNotFoundException() {
        booking.setStatus(Status.WAITING);
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        assertThrows(NotFoundException.class, () -> {
            bookingService.approveBooking(1L, 2L, true);
        });
    }

    @Test
    void approveBookingApprovedToApprovedCatchValidationException() {
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        assertThrows(ValidationException.class, () -> {
            bookingService.approveBooking(1L, 1L, true);
        });
    }

    @Test
    void getBooking() {
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        BookingDto resultDto = bookingService.getBooking(1L, 1L);
        verify(bookingRepository).findById(anyLong());
        assertEquals(booking.getId(), resultDto.getId());
    }

    @Test
    void getBookingNotOwnerNotRequester() {
        booking.setBooker(requester);
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        assertThrows(NotFoundException.class, () -> {
            bookingService.getBooking(1L, 3L);
        });
    }

    @Test
    void getBookings() {
        int from = 0;
        int size = 1;
        PageRequest page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(bookingRepository.findAllByBookerId(1L, page))
                .thenReturn(bookings);
        List<BookingDto> resultBookings = bookingService.getBookings(1L, Status.ALL, from, size);
        verify(bookingRepository).findAllByBookerId(anyLong(), any(PageRequest.class));
        assertEquals(1, resultBookings.size());
    }

    @Test
    void getPastBookings() {
        int from = 0;
        int size = 1;
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        PageRequest page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(bookingRepository.findAllByBookerId(1L, page))
                .thenReturn(bookings);
        List<BookingDto> resultBookings = bookingService.getBookings(1L, Status.PAST, from, size);
        verify(bookingRepository).findAllByBookerId(anyLong(), any(PageRequest.class));
        assertEquals(1, resultBookings.size());
    }

    @Test
    void getCurrentBookings() {
        int from = 0;
        int size = 1;
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        PageRequest page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(bookingRepository.findAllByBookerId(1L, page))
                .thenReturn(bookings);
        List<BookingDto> resultBookings = bookingService.getBookings(1L, Status.CURRENT, from, size);
        verify(bookingRepository).findAllByBookerId(anyLong(), any(PageRequest.class));
        assertEquals(1, resultBookings.size());
    }

    @Test
    void getFutureBookings() {
        int from = 0;
        int size = 1;
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(3));
        PageRequest page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(bookingRepository.findAllByBookerId(1L, page))
                .thenReturn(bookings);
        List<BookingDto> resultBookings = bookingService.getBookings(1L, Status.FUTURE, from, size);
        verify(bookingRepository).findAllByBookerId(anyLong(), any(PageRequest.class));
        assertEquals(1, resultBookings.size());
    }

    @Test
    void getBookingsByState() {
        int from = 0;
        int size = 1;
        PageRequest page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        booking.setStatus(Status.APPROVED);
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStatus(1L, Status.APPROVED, page))
                .thenReturn(bookings);
        List<BookingDto> resultBookings = bookingService.getBookings(1L, Status.APPROVED, from, size);
        verify(bookingRepository).findAllByBookerIdAndStatus(1L, Status.APPROVED, page);
        assertEquals(1, resultBookings.size());
    }

    @Test
    void getOwnerBookingsByState() {
        int from = 0;
        int size = 1;
        booking.setStatus(Status.APPROVED);
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(bookingRepository.findAllByOwnerIdAndStatus(anyLong(), anyString(), any(PageRequest.class)))
                .thenReturn(bookings);
        List<BookingDto> resultBookings = bookingService.getOwnerBookings(1L, Status.APPROVED, from, size);
        verify(bookingRepository).findAllByOwnerIdAndStatus(anyLong(), anyString(), any(PageRequest.class));
        assertEquals(1, resultBookings.size());
    }

    @Test
    void getOwnerBookings() {
        int from = 0;
        int size = 1;
        booking.setStatus(Status.APPROVED);
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(bookingRepository.findAllByOwnerId(anyLong(), any(PageRequest.class)))
                .thenReturn(bookings);
        List<BookingDto> resultBookings = bookingService.getOwnerBookings(1L, Status.ALL, from, size);
        verify(bookingRepository).findAllByOwnerId(anyLong(), any(PageRequest.class));
        assertEquals(1, resultBookings.size());
    }

    @Test
    void getOwnerBookingsPast() {
        int from = 0;
        int size = 1;
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(Status.APPROVED);
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(bookingRepository.findAllByOwnerId(anyLong(), any(PageRequest.class)))
                .thenReturn(bookings);
        List<BookingDto> resultBookings = bookingService.getOwnerBookings(1L, Status.PAST, from, size);
        verify(bookingRepository).findAllByOwnerId(anyLong(), any(PageRequest.class));
        assertEquals(1, resultBookings.size());
    }

    @Test
    void getOwnerBookingsFuture() {
        int from = 0;
        int size = 1;
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(3));
        booking.setStatus(Status.APPROVED);
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(bookingRepository.findAllByOwnerId(anyLong(), any(PageRequest.class)))
                .thenReturn(bookings);
        List<BookingDto> resultBookings = bookingService.getOwnerBookings(1L, Status.FUTURE, from, size);
        verify(bookingRepository).findAllByOwnerId(anyLong(), any(PageRequest.class));
        assertEquals(1, resultBookings.size());
    }

    @Test
    void getOwnerBookingsCurrent() {
        int from = 0;
        int size = 1;
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(Status.APPROVED);
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(bookingRepository.findAllByOwnerId(anyLong(), any(PageRequest.class)))
                .thenReturn(bookings);
        List<BookingDto> resultBookings = bookingService.getOwnerBookings(1L, Status.CURRENT, from, size);
        verify(bookingRepository).findAllByOwnerId(anyLong(), any(PageRequest.class));
        assertEquals(1, resultBookings.size());
    }
}