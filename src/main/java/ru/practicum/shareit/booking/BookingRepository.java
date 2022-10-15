package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long ownerId, Status status);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long ownerId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM bookings b WHERE b.item_id IN"
                    + " (SELECT id FROM items i WHERE i.owner_id = ?1)"
                    + " ORDER BY b.start_date desc")
    List<Booking> findAllByOwnerIdOrderByStartDesc(Long ownerId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM bookings b WHERE b.item_id IN"
                    + " (SELECT id FROM items i WHERE i.owner_id = ?1)"
                    + " AND status = ?2"
                    + " ORDER BY b.start_date desc")
    List<Booking> findAllByOwnerIdAndStatusOrderByStartDesc(Long ownerId, String status);


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
}
