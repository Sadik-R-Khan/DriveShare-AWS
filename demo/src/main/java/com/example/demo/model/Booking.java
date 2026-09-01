package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name="bookings")
@Getter
@Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name ="user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="car_id", nullable = false)
    private Car car;


    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    private String pickupLocation;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
        if(this.car != null && this.startDate !=null && this.endDate !=null) {
            long days = Math.max(1, ChronoUnit.DAYS.between(this.startDate, this.endDate));
            this.totalAmount=this.car.getPricePerDay().multiply(BigDecimal.valueOf(days));
        }
    }

    public enum BookingStatus{ PENDING, CONFIRMED, CANCELLED, COMPLETED}
}
