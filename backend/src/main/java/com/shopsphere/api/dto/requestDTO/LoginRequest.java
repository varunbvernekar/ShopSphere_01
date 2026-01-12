package com.shopsphere.api.dto.requestDTO;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
