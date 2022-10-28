package ru.practicum.shareit.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class RequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemRequestDto> items;
}
