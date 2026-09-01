package com.example.demo.dto;

import com.example.demo.model.Booking;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor @NoArgsConstructor @Setter @Getter
public class BookingDto {
    private Long id;
    @NotNull private Long carId;
    @NotNull private LocalDate startDate;
    @NotNull private LocalDate endDate;
    private BigDecimal totalAmount;
    private Booking.BookingStatus status;
    private String pickupLocation;
    private Long userId;
    private String userName;
    private String carMake;
    private String carModel;
    private LocalDateTime createdAt;
}