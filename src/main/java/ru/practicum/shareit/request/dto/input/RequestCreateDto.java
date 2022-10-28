package ru.practicum.shareit.request.dto.input;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.marker.Create;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class RequestCreateDto {

    @NotNull(groups = {Create.class})
    private String description;
}
