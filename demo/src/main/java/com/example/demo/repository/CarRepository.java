package com.example.demo.repository;

import com.example.demo.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByStatus(Car.CarStatus status);
    List<Car> findByRenterId(Long renterId);
    List<Car> findByMakeContainingIgnoreCaseOrModelContainingIgnoreCase(String make, String model);
}
