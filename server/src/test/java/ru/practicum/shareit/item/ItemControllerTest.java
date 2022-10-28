package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    ItemService itemService;

    @Autowired
    MockMvc mockMvc;

    ItemDto stub;
    ItemWithBookingsDto outputStub;
    ObjectMapper json;

    @BeforeEach
    void beforeEach() {
        stub = ItemDto.builder()
                .name("просто ")
                .description(" о сложном")
                .available(true)
                .build();
        outputStub = ItemWithBookingsDto.builder().build();
        json = new ObjectMapper();
    }

    @SneakyThrows
    @Test
    void createItem() {
        when(itemService.createItem(any(ItemDto.class), anyLong()))
                .thenReturn(stub);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .content(json.writeValueAsString(stub)))
                .andExpect(status().isOk());
        verify(itemService).createItem(any(ItemDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    void updateItem() {
        when(itemService.updateItem(any(ItemDto.class), anyLong(), anyLong()))
                .thenReturn(stub);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .content(json.writeValueAsString(stub)))
                .andExpect(status().isOk());
        verify(itemService).updateItem(any(ItemDto.class), anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getItem() {
        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(outputStub);

        mockMvc.perform(get("/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", "1")
        ).andExpect(status().isOk());
        verify(itemService).getItem(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getItems() {
        when(itemService.getItems(anyLong()))
                .thenReturn(List.of(outputStub));

        mockMvc.perform(get("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", "1")
        ).andExpect(status().isOk());
        verify(itemService).getItems(anyLong());
    }

    @SneakyThrows
    @Test
    void searchItems() {
        when(itemService.searchItemsByText(anyString()))
                .thenReturn(List.of(stub));

        mockMvc.perform(get("/items/search?text=abra")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", "1")
        ).andExpect(status().isOk());
        verify(itemService).searchItemsByText(anyString());
    }

    @SneakyThrows
    @Test
    void postComment() {
        when(itemService.postComment(anyLong(), anyLong(), any(CreateCommentDto.class)))
                .thenReturn(CommentDto.builder().build());

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .content("{\"text\":\"комментарий\"}"))
                .andExpect(status().isOk());
        verify(itemService).postComment(anyLong(), anyLong(), any(CreateCommentDto.class));
    }
}