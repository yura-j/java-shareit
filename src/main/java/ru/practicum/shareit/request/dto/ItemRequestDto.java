package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requesterId;
    private String created;
}
