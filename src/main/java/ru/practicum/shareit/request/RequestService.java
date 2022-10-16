package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.input.RequestCreateDto;
import ru.practicum.shareit.request.dto.output.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public RequestDto create(RequestCreateDto dto, Long ownerId) {
        User requester = userRepository.findById(ownerId).orElseThrow(NotFoundException::new);
        ItemRequest request = RequestMapper.fromDto(dto, requester);

        requestRepository.save(request);
        RequestMapper.fromRequest(request);

        return RequestMapper.fromRequest(request);
    }

    public List<RequestDto> getMy(Long ownerId) {
        User requester = userRepository.findById(ownerId).orElseThrow(NotFoundException::new);
        List<ItemRequest> requests = requestRepository.findAllByrequester_id(requester.getId());
        return requests.stream()
                .map(RequestMapper::fromRequest)
                .collect(Collectors.toList());
    }

    public List<RequestDto> getAllExceptMy(Long ownerId, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        User requester = userRepository.findById(ownerId).orElseThrow(NotFoundException::new);

        Page<ItemRequest> requests = requestRepository.findAllByrequester_idNot(requester.getId(), page);
        return requests.stream()
                .map(RequestMapper::fromRequest)
                .collect(Collectors.toList());
    }

    public RequestDto get(Long requestId, Long ownerId) {
        User requester = userRepository.findById(ownerId).orElseThrow(NotFoundException::new);

        ItemRequest request = requestRepository.findById(requestId).orElseThrow(NotFoundException::new);
        return RequestMapper.fromRequest(request);
    }
}
