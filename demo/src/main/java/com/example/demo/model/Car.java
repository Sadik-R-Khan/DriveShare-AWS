package com.example.demo.model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name= "cars")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Builder
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "renter_id", nullable = false)
    private Renter renter;

    @Column(nullable = false)
    private String make;

    @Column(nullable = false)
    private String model;

    @Column(name = "manufacture_year")
    private Integer year;
    private String color;
    private String licensePlate;

    @Column(nullable = false)
    private BigDecimal pricePerDay;

    @Column(columnDefinition = "LONGTEXT")
    private String imageData;

    private String description;
    private String location;
    private Integer seatingCapacity;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    private TransmissionType transmission;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarStatus status;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }

    public enum FuelType{ PETROL, DIESEL, ELECTRIC, HYBRID }
    public enum TransmissionType{ MANUAL, AUTOMATIC}
    public enum CarStatus{ AVAILABLE, RENTED, MAINTENANCE}

}
