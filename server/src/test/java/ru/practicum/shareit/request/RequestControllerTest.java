package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.request.dto.input.RequestCreateDto;
import ru.practicum.shareit.request.dto.output.ItemRequestDto;
import ru.practicum.shareit.request.dto.output.RequestDto;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class RequestControllerTest {

    @MockBean
    RequestService requestService;
    @Autowired
    MockMvc mockMvc;

    RequestCreateDto createStub;
    RequestDto stub;
    ItemRequestDto itemStub;
    ObjectMapper json;

    @BeforeEach
    void beforeAll() {
        createStub = new RequestCreateDto();
        itemStub = ItemRequestDto.builder().build();
        stub = RequestDto.builder().build();
        json = new ObjectMapper();
    }

    @SneakyThrows
    @Test
    void createRequest() {
        createStub.setDescription("valera");
        when(requestService.create(Mockito.any(RequestCreateDto.class), Mockito.anyLong()))
                .thenReturn(stub);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .content(json.writeValueAsString(createStub)))
                .andExpect(status().isOk());
        verify(requestService).create(Mockito.any(RequestCreateDto.class), Mockito.anyLong());
    }

    @SneakyThrows
    @Test
    void getMy() {
        when(requestService.getMy(Mockito.anyLong()))
                .thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
        verify(requestService).getMy(Mockito.anyLong());
    }

    @SneakyThrows
    @Test
    void getAllExceptMy() {
        when(requestService.getAllExceptMy(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
        verify(requestService).getAllExceptMy(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @SneakyThrows
    @Test
    void negativeFromIn400Out() {
        when(requestService.getAllExceptMy(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "-1")
                )
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void negativeSizeIn400Out() {
        when(requestService.getAllExceptMy(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("size", "-1")
                )
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void get() {
        when(requestService.get(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(stub);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
        verify(requestService).get(Mockito.anyLong(), Mockito.anyLong());
    }
}