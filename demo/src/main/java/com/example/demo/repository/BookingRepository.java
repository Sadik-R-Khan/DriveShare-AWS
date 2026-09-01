package com.example.demo.repository;


import com.example.demo.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.car.id = :carId " +
            "AND b.status IN ('PENDING','CONFIRMED') " +
            "AND (b.startDate < :endDate AND b.endDate > :startDate)")
    List<Booking> findConflictingBookings(@Param("carId") Long carId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    @Query("SELECT b FROM Booking b WHERE b.car.renter.id = :renterId")
    List<Booking> findByRenterId(@Param("renterId") Long renterId);
}
