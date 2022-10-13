package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.marker.Create;

import javax.validation.constraints.NotBlank;

@Data
public class CreateCommentDto {

    @NotBlank(groups = {Create.class})
    String text;
}
