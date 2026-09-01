package com.example.demo.repository;

import com.example.demo.model.Renter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RenterRepository extends JpaRepository<Renter, Long> {
    Optional<Renter> findByUserId(Long userId);
}