package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.input.RequestCreateDto;
import ru.practicum.shareit.request.dto.output.ItemRequestDto;
import ru.practicum.shareit.request.dto.output.RequestDto;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface RequestMapper {
    static Request fromDto(RequestCreateDto dto, User requester) {
        return Request.builder()
                .description(dto.getDescription())
                .requester(requester)
                .build();
    }

    static RequestDto fromRequest(Request request) {
        List<ItemRequestDto> items = (null == request.getItems())
                ? new ArrayList<>()
                : request.getItems().stream()
                .map(ItemMapper::toItemRequestDto)
                .collect(Collectors.toList());
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(items)
                .build();
    }
}
