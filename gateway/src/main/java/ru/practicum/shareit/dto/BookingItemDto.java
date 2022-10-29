package ru.practicum.shareit.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class BookingItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
}
