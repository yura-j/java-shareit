package ru.practicum.shareit.item;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.marker.Create;

import javax.validation.constraints.NotBlank;

@Data
@Getter
@Setter
public class CreateCommentDto {

    @NotBlank(groups = {Create.class})
    String text;
}
