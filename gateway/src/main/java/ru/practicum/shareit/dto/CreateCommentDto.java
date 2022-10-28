package ru.practicum.shareit.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.marker.Create;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CreateCommentDto {

    @NotBlank(groups = {Create.class})
    String text;
}
