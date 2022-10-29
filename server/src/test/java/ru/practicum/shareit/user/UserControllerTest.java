package ru.practicum.shareit.user;


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
import ru.practicum.shareit.error.AlreadyExistException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    UserService userService;
    @Autowired
    MockMvc mockMvc;

    UserDto stub;
    ObjectMapper json;

    @BeforeEach
    void beforeAll() {
        stub = UserDto.builder().build();
        json = new ObjectMapper();
    }


    @SneakyThrows
    @Test
    void getUsers() {
        when(userService.getUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(userService).getUsers();
    }

    @SneakyThrows
    @Test
    void getUser() {
        when(userService.getUser(1L)).thenReturn(stub);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(json.writeValueAsString(stub)));
        verify(userService).getUser(Mockito.anyLong());
    }

    @SneakyThrows
    @Test
    void createUser() {
        stub.setName("valera");
        stub.setEmail("valera@valera.valera");
        when(userService.createUser(stub)).thenReturn(stub);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(stub)))
                .andExpect(status().isOk());
        verify(userService).createUser(Mockito.any(UserDto.class));
    }

    @Test
    void updateDoubleMail() throws Exception {
        stub.setName("valera");
        stub.setEmail("valera@valera.valera");
        when(userService.updateUser(Mockito.any(UserDto.class), Mockito.anyLong()))
                .thenThrow(new AlreadyExistException("test"));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(stub)))
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void updateUser() {
        stub.setName("valera");
        stub.setEmail("valera@valera.valera");
        when(userService.updateUser(stub, 1L))
                .thenReturn(stub);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(stub)))
                .andExpect(status().isOk());

        verify(userService).updateUser(Mockito.any(UserDto.class), Mockito.anyLong());
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1")).andExpect(status().isOk());
        verify(userService).deleteUser(Mockito.anyLong());
    }
}