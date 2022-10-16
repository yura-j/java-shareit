package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequester(User requester);

    List<ItemRequest> findAllByrequester_id(Long id);

    Page<ItemRequest> findAllByrequester_idNot(Long id, PageRequest page);
}
