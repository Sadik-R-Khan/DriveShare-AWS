package com.example.demo.dto;

import com.example.demo.model.Car;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CarDto {

    private Long id;
    @NotBlank private String make;
    @NotBlank private String model;
    private Integer year;
    private String color;
    private String licensePlate;
    @NotNull
    @Positive
    private BigDecimal pricePerDay;
    private String imageData;
    private String description;
    private String location;
    private Integer seatingCapacity;
    private Car.FuelType fuelType;
    private Car.TransmissionType transmission;
    private Car.CarStatus status;
    private Long renterId;
    private String renterName;
}
