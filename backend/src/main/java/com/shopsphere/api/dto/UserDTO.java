package com.shopsphere.api.dto;

import com.shopsphere.api.domain.enums.UserRole;
import com.shopsphere.api.entity.Address;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private Address address;
    private String gender;
    private LocalDate dateOfBirth;
}
