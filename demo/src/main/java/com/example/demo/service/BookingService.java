package com.example.demo.service;


import com.example.demo.dto.BookingDto;
import com.example.demo.model.Booking;
import com.example.demo.model.Car;
import com.example.demo.model.User;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public BookingDto createBooking(BookingDto dto, Long userId) {
        if (dto.getStartDate().isAfter(dto.getEndDate())
                || dto.getStartDate().equals(dto.getEndDate())) {
            throw new RuntimeException("End date must be after start date");
        }
        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));
        if (car.getStatus() != Car.CarStatus.AVAILABLE) {
            throw new RuntimeException("Car is not available");
        }
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                dto.getCarId(), dto.getStartDate(), dto.getEndDate());
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Car is not available for selected dates");
        }
        User user = userRepository.findById(userId).orElseThrow();
        Booking booking = Booking.builder()
                .user(user)
                .car(car)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .status(Booking.BookingStatus.PENDING)
                .pickupLocation(dto.getPickupLocation())
                .build();
        return toDto(bookingRepository.save(booking));
    }

    public List<BookingDto> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<BookingDto> getBookingsByRenter(Long renterId) {
        return bookingRepository.findByRenterId(renterId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public BookingDto updateStatus(Long id, Booking.BookingStatus status) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        booking.setStatus(status);
        return toDto(bookingRepository.save(booking));
    }

    public void cancelBooking(Long id, Long userId) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    private BookingDto toDto(Booking b) {
        BookingDto dto = new BookingDto();
        dto.setId(b.getId());
        dto.setCarId(b.getCar().getId());
        dto.setStartDate(b.getStartDate());
        dto.setEndDate(b.getEndDate());
        dto.setTotalAmount(b.getTotalAmount());
        dto.setStatus(b.getStatus());
        dto.setPickupLocation(b.getPickupLocation());
        dto.setUserId(b.getUser().getId());
        dto.setUserName(b.getUser().getName());
        dto.setCarMake(b.getCar().getMake());
        dto.setCarModel(b.getCar().getModel());
        dto.setCreatedAt(b.getCreatedAt());
        return dto;
    }
}
