package ru.practicum.shareit.error;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {
    String error;
    List<String> parts;

    public ErrorResponse(String error) {
        this.error = error;
    }
}
