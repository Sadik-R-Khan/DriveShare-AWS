package com.example.demo.controller;


import com.example.demo.dto.BookingDto;
import com.example.demo.model.Booking;
import com.example.demo.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody BookingDto dto,
                                                    @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(bookingService.createBooking(dto, userId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingDto>> myBookings(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }

    @GetMapping("/renter/{renterId}")
    public ResponseEntity<List<BookingDto>> renterBookings(@PathVariable Long renterId) {
        return ResponseEntity.ok(bookingService.getBookingsByRenter(renterId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BookingDto> updateStatus(@PathVariable Long id,
                                                   @RequestParam Booking.BookingStatus status) {
        return ResponseEntity.ok(bookingService.updateStatus(id, status));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id,
                                              @RequestAttribute("userId") Long userId) {
        bookingService.cancelBooking(id, userId);
        return ResponseEntity.noContent().build();
    }
}
