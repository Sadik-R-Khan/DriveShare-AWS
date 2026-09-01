package com.example.demo.controller;


import com.example.demo.dto.RenterDto;
import com.example.demo.service.RenterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/renters")
@RequiredArgsConstructor
public class RenterController {

    private final RenterService renterService;

    @PostMapping
    public ResponseEntity<RenterDto> createProfile(@RequestBody RenterDto dto,
                                                   @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(renterService.createRenterProfile(dto, userId));
    }

    @GetMapping("/me")
    public ResponseEntity<RenterDto> getMyProfile(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(renterService.getRenterByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RenterDto> updateProfile(@PathVariable Long id,
                                                   @RequestBody RenterDto dto) {
        return ResponseEntity.ok(renterService.updateRenterProfile(id, dto));
    }
}
