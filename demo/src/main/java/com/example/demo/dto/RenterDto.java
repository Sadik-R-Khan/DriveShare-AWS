package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RenterDto {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String businessName;
    private String address;
    private String licenseNumber;
}