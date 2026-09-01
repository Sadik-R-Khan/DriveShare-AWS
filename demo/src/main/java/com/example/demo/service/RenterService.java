package com.example.demo.service;


import com.example.demo.dto.RenterDto;
import com.example.demo.model.Renter;
import com.example.demo.model.User;
import com.example.demo.repository.RenterRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RenterService {

    private final RenterRepository renterRepository;
    private final UserRepository userRepository;

    public RenterDto createRenterProfile(RenterDto dto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        if (renterRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("Renter profile already exists");
        }
        if (user.getRole() == User.Role.USER) {
            user.setRole(User.Role.RENTER);
            userRepository.save(user);
        }
        Renter renter = Renter.builder()
                .user(user)
                .businessName(dto.getBusinessName())
                .address(dto.getAddress())
                .licenseNumber(dto.getLicenseNumber())
                .build();
        return toDto(renterRepository.save(renter));
    }

    public RenterDto getRenterByUserId(Long userId) {
        Renter renter = renterRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Renter profile not found"));
        return toDto(renter);
    }

    public RenterDto updateRenterProfile(Long id, RenterDto dto) {
        Renter renter = renterRepository.findById(id).orElseThrow();
        renter.setBusinessName(dto.getBusinessName());
        renter.setAddress(dto.getAddress());
        renter.setLicenseNumber(dto.getLicenseNumber());
        return toDto(renterRepository.save(renter));
    }

    private RenterDto toDto(Renter r) {
        RenterDto dto = new RenterDto();
        dto.setId(r.getId());
        dto.setUserId(r.getUser().getId());
        dto.setUserName(r.getUser().getName());
        dto.setUserEmail(r.getUser().getEmail());
        dto.setBusinessName(r.getBusinessName());
        dto.setAddress(r.getAddress());
        dto.setLicenseNumber(r.getLicenseNumber());
        return dto;
    }
}
