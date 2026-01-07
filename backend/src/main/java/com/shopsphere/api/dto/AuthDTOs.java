package com.shopsphere.api.dto;

import com.shopsphere.api.domain.enums.UserRole;
import com.shopsphere.api.entity.Address;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

public class AuthDTOs {

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    public static class RegisterRequest {
        private String name;
        private String email;
        private String password;
        private String phoneNumber;
        private UserRole role; // Optional, defaults to CUSTOMER
        private Address address;
        private String gender;
        private LocalDate dateOfBirth;
    }

    @Data
    @Builder
    public static class AuthResponse {
        private String token;
        private UserDTO user;
    }
}
