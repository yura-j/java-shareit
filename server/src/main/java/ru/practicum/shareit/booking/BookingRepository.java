package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBookerIdAndStatus(Long ownerId, Status status, PageRequest page);

    Page<Booking> findAllByBookerId(Long ownerId, PageRequest page);

    @Query(nativeQuery = true,
            value = "SELECT * FROM bookings b WHERE b.item_id = ?1 and b.booker_id != ?2"
                    + " ORDER BY id"
                    + " LIMIT 1")
    Booking findLastByItemIdAndNotOwnerId(Long id, Long ownerId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM bookings b WHERE b.item_id = ?1 and b.booker_id != ?2"
                    + " ORDER BY id"
                    + " LIMIT 1,2")
    Booking findNextByItemIdAndNotOwnerId(Long id, Long ownerId);


    List<Booking> findByItemId(Long itemId);


    @Query(nativeQuery = true,
            value = "SELECT * FROM bookings b WHERE b.item_id IN"
                    + " (SELECT id FROM items i WHERE i.owner_id = ?1)"
                    + " AND status = ?2",
            countQuery = "select count(*) FROM bookings b " +
                    "WHERE b.item_id IN (SELECT id FROM items i WHERE i.owner_id = ?)  AND status = ?"
    )
    Page<Booking> findAllByOwnerIdAndStatus(Long ownerId, String name, Pageable page);

    @Query(nativeQuery = true,
            value = "SELECT * FROM bookings b WHERE b.item_id IN"
                    + " (SELECT id FROM items i WHERE i.owner_id = ?1)",
            countQuery = "select count(*) FROM bookings b " +
                    "WHERE b.item_id IN (SELECT id FROM items i WHERE i.owner_id = ?)"
    )
    Page<Booking> findAllByOwnerId(Long ownerId, Pageable page);
}
