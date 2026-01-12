package com.shopsphere.api.dto.requestDTO;

import com.shopsphere.api.entity.Address;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateRequest {
    private String name;
    private String phoneNumber;
    private Address address;
    private String gender;
    private LocalDate dateOfBirth;
}
