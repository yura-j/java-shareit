package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private TestEntityManager em;


    @BeforeEach
    void setUp() {

        User owner = userRepository.save(new User(null, "valera", "valera@valera.valera"));
        User requester = userRepository.save(new User(null, "valera", "valera1@valera.valera"));
        User sideUser = userRepository.save(new User(null, "valera", "valera2@valera.valera"));

        Request request = requestRepository.save(new Request(null,
                "description",
                LocalDateTime.now(),
                owner,
                null));

        Item item = new Item(null,
                "item",
                "description",
                true,
                owner,
                request,
                List.of(),
                List.of());
        itemRepository.save(item);
        Booking booking = bookingRepository.save(new Booking(null,
                Status.APPROVED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                item, requester));
    }

    @Test
    void findAllByBookerIdNotFound() {
        Page<Booking> data = bookingRepository.findAllByBookerId(1L,
                PageRequest.of(0, 1));
        assertEquals(0, data.getTotalElements());
    }

    @Test
    void findAllByBookerId() {
        Page<Booking> data = bookingRepository.findAllByBookerId(2L,
                PageRequest.of(0, 1));
        assertEquals(1, data.getTotalElements());
    }

    @Test
    void findByItemId() {
        List<Booking> data = bookingRepository.findByItemId(1L);
        assertEquals(1, data.size());
    }

    @Test
    void findByItemIdNotFound() {
        List<Booking> data = bookingRepository.findByItemId(2L);
        assertEquals(0, data.size());
    }

    @Test
    void findAllByOwnerId() {
        Page<Booking> data = bookingRepository.findAllByOwnerId(1L,
                PageRequest.of(0, 1));
        assertEquals(1, data.getTotalElements());
    }

    @Test
    void findAllByOwnerIdNotFound() {
        Page<Booking> data = bookingRepository.findAllByOwnerId(2L,
                PageRequest.of(0, 1));
        assertEquals(0, data.getTotalElements());
    }

    @Test
    void findAllByOwnerIdAndStatusInvalidStatusNotFound() {
        Page<Booking> data = bookingRepository.findAllByOwnerIdAndStatus(1L, "REJECTED",
                PageRequest.of(0, 1));
        assertEquals(0, data.getTotalElements());
    }

    @Test
    void findAllByOwnerIdAndStatusInvalidOwnerNotFound() {
        Page<Booking> data = bookingRepository.findAllByOwnerIdAndStatus(2L, "APPROVED",
                PageRequest.of(0, 1));
        assertEquals(0, data.getTotalElements());
    }

    @Test
    void findAllByOwnerIdAndStatus() {
        Page<Booking> data = bookingRepository.findAllByOwnerIdAndStatus(1L, "APPROVED",
                PageRequest.of(0, 1));
        assertEquals(1, data.getTotalElements());
    }
}