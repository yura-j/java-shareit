package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.error.HasNoAccessException;
import ru.practicum.shareit.error.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper json;
    private BookingCreateDto createDto;
    private BookingShortDto bookingShortDto;
    private BookingDto bookingDto;
    private BookingItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        json = new ObjectMapper();
        createDto = BookingCreateDto.builder().build();
        itemDto = BookingItemDto.builder().build();
        bookingDto = BookingDto.builder().build();
        bookingShortDto = BookingShortDto.builder().build();

    }

    @SneakyThrows
    @Test
    void createBooking() {
        when(bookingService.createBooking(any(BookingCreateDto.class), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .content(json.writeValueAsString(createDto)))
                .andExpect(status().isOk());
        verify(bookingService).createBooking(any(BookingCreateDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    void updateBooking() {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk());
        verify(bookingService).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @SneakyThrows
    @Test
    void getBooking() {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk());
        verify(bookingService).getBooking(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getBookings() {
        when(bookingService.getBookings(anyLong(), any(Status.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk());
        verify(bookingService).getBookings(anyLong(), any(Status.class), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getBookingsCatchIllegalArgumentException() {
        when(bookingService.getBookings(anyLong(), any(Status.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings?state=BIG_BUM")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void getBookingsCatchValidationSizeException() {
        when(bookingService.getBookings(anyLong(), any(Status.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings?size=-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void getBookingsCatchValidationFromException() {
        when(bookingService.getBookings(anyLong(), any(Status.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings?from=-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().is4xxClientError());
    }


    @SneakyThrows
    @Test
    void getOwnerBookings() {
        when(bookingService.getOwnerBookings(anyLong(), any(Status.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk());
        verify(bookingService).getOwnerBookings(anyLong(), any(Status.class), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getOwnerBookingsWrongStateCatchIllegalException() {
        when(bookingService.getOwnerBookings(anyLong(), any(Status.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner?state=BIG_BUM")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void getOwnerBookingsWrongSizeCatchValidationExceptions() {
        when(bookingService.getOwnerBookings(anyLong(), any(Status.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner?size=-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void getOwnerBookingsWrongFromCatchValidationExceptions() {
        when(bookingService.getOwnerBookings(anyLong(), any(Status.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner?from=-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void catchNotFoundException() {
        when(bookingService.getOwnerBookings(anyLong(), any(Status.class), anyInt(), anyInt()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void catchHasNoAccessException() {
        when(bookingService.getOwnerBookings(anyLong(), any(Status.class), anyInt(), anyInt()))
                .thenThrow(HasNoAccessException.class);

        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void catchNoSuchElementException() {
        when(bookingService.getOwnerBookings(anyLong(), any(Status.class), anyInt(), anyInt()))
                .thenThrow(NoSuchElementException.class);

        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void catchConstraintViolationException() {
        when(bookingService.getOwnerBookings(anyLong(), any(Status.class), anyInt(), anyInt()))
                .thenThrow(ConstraintViolationException.class);

        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().is4xxClientError());
    }

}