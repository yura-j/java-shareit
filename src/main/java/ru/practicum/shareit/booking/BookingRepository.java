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

/*    @Query(nativeQuery = true,
            value = "SELECT * FROM bookings b WHERE b.item_id IN"
                    + " (SELECT id FROM items i WHERE i.owner_id = ?1)"
                    + " ORDER BY b.start_date desc"
                    + " LIMIT ?2, ?3")
    Page<Booking> findAllByOwnerIdOrderByStartDesc(Long ownerId, Integer from, Integer size);

    @Query(nativeQuery = true,
            value = "SELECT * FROM bookings b WHERE b.item_id IN"
                    + " (SELECT id FROM items i WHERE i.owner_id = ?1)"
                    + " AND status = ?2"
                    + " ORDER BY b.start_date desc"
                    + " LIMIT ?3, ?4")
    Page<Booking> findAllByOwnerIdAndStatusOrderByStartDesc(Long ownerId, String status, Integer from, Integer size);*/


    Booking findTopByItemId(Long itemId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM bookings b WHERE b.item_id = ?1"
                    + " ORDER BY abs(b.start_date - CURRENT_TIMESTAMP)"
                    + " LIMIT 1")
    Booking findClosestByItemId(Long ownerId);

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

    List<Booking> findByItem(Long itemId);

    List<Booking> findByItemId(Long itemId);


    @Query(nativeQuery = true,
            value = "SELECT * FROM bookings b WHERE b.item_id IN"
                    + " (SELECT id FROM items i WHERE i.owner_id = ?1)"
                    + " AND status = ?2")
    Page<Booking> findAllByOwnerIdAndStatus(Long ownerId, String name, Pageable page);

    @Query(nativeQuery = true,
            value = "SELECT * FROM bookings b WHERE b.item_id IN"
                    + " (SELECT id FROM items i WHERE i.owner_id = ?1)",
            countQuery = "select count(*) FROM bookings b WHERE b.item_id IN (SELECT id FROM items i WHERE i.owner_id = ?)"
    )
    Page<Booking> findAllByOwnerId(Long ownerId, Pageable page);
}
