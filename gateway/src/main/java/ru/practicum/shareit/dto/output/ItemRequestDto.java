package ru.practicum.shareit.dto.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ItemRequestDto {
    private Long id;
    private String name;
    private String description;
    private Long requestId;
    private Boolean available;
}
