package com.example.demo.service;


import com.example.demo.dto.CarDto;
import com.example.demo.model.Car;
import com.example.demo.model.Renter;
import com.example.demo.model.User;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.RenterRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final RenterRepository renterRepository;
    private final UserRepository userRepository;

    public List<CarDto> getAllAvailableCars() {
        return carRepository.findByStatus(Car.CarStatus.AVAILABLE)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<CarDto> getAllCars() {
        return carRepository.findAll()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public CarDto getCarById(Long id) {
        return toDto(carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found")));
    }

    public List<CarDto> getCarsByRenter(Long renterId) {
        return carRepository.findByRenterId(renterId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public CarDto createCar(CarDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Renter renter = resolveRenter(dto, user);
        Car car = Car.builder()
                .renter(renter)
                .make(dto.getMake())
                .model(dto.getModel())
                .year(dto.getYear())
                .color(dto.getColor())
                .licensePlate(dto.getLicensePlate())
                .pricePerDay(dto.getPricePerDay())
                .imageData(dto.getImageData())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .seatingCapacity(dto.getSeatingCapacity())
                .fuelType(dto.getFuelType())
                .transmission(dto.getTransmission())
                .status(Car.CarStatus.AVAILABLE)
                .build();
        return toDto(carRepository.save(car));
    }

    private Renter resolveRenter(CarDto dto, User user) {
        if (user.getRole() == User.Role.ADMIN && dto.getRenterId() != null) {
            return renterRepository.findById(dto.getRenterId())
                    .orElseThrow(() -> new RuntimeException("Renter profile not found"));
        }
        return renterRepository.findByUserId(user.getId())
                .orElseGet(() -> createDefaultRenterProfile(user));
    }

    private Renter createDefaultRenterProfile(User user) {
        if (user.getRole() != User.Role.RENTER && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Only renters can create cars");
        }
        return renterRepository.save(Renter.builder()
                .user(user)
                .businessName(user.getName())
                .build());
    }

    public CarDto updateCar(Long id, CarDto dto) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found"));
        car.setMake(dto.getMake());
        car.setModel(dto.getModel());
        car.setYear(dto.getYear());
        car.setColor(dto.getColor());
        car.setLicensePlate(dto.getLicensePlate());
        car.setPricePerDay(dto.getPricePerDay());
        car.setDescription(dto.getDescription());
        car.setLocation(dto.getLocation());
        car.setSeatingCapacity(dto.getSeatingCapacity());
        car.setFuelType(dto.getFuelType());
        car.setTransmission(dto.getTransmission());
        if (dto.getImageData() != null) car.setImageData(dto.getImageData());
        if (dto.getStatus() != null) car.setStatus(dto.getStatus());
        return toDto(carRepository.save(car));
    }

    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    public List<CarDto> searchCars(String query) {
        return carRepository
                .findByMakeContainingIgnoreCaseOrModelContainingIgnoreCase(query, query)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private CarDto toDto(Car car) {
        CarDto dto = new CarDto();
        dto.setId(car.getId());
        dto.setMake(car.getMake());
        dto.setModel(car.getModel());
        dto.setYear(car.getYear());
        dto.setColor(car.getColor());
        dto.setLicensePlate(car.getLicensePlate());
        dto.setPricePerDay(car.getPricePerDay());
        dto.setImageData(car.getImageData());
        dto.setDescription(car.getDescription());
        dto.setLocation(car.getLocation());
        dto.setSeatingCapacity(car.getSeatingCapacity());
        dto.setFuelType(car.getFuelType());
        dto.setTransmission(car.getTransmission());
        dto.setStatus(car.getStatus());
        dto.setRenterId(car.getRenter().getId());
        dto.setRenterName(car.getRenter().getUser().getName());
        return dto;
    }
}
