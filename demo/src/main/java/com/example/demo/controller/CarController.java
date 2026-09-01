package com.example.demo.controller;


import com.example.demo.dto.CarDto;
import com.example.demo.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping
    public ResponseEntity<List<CarDto>> getAvailableCars() {
        return ResponseEntity.ok(carService.getAllAvailableCars());
    }

    @GetMapping("/all")
    public ResponseEntity<List<CarDto>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarDto> getCarById(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CarDto>> searchCars(@RequestParam String query) {
        return ResponseEntity.ok(carService.searchCars(query));
    }

    @GetMapping("/renter/{renterId}")
    public ResponseEntity<List<CarDto>> getCarsByRenter(@PathVariable Long renterId) {
        return ResponseEntity.ok(carService.getCarsByRenter(renterId));
    }

    @PostMapping
    public ResponseEntity<CarDto> createCar(@Valid @RequestBody CarDto dto,
                                            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(carService.createCar(dto, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarDto> updateCar(@PathVariable Long id,
                                            @Valid @RequestBody CarDto dto) {
        return ResponseEntity.ok(carService.updateCar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}
