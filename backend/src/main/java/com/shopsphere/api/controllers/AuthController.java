package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.AuthDTOs;
import com.shopsphere.api.dto.UserDTO;
import com.shopsphere.api.entity.User;
import com.shopsphere.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final com.shopsphere.api.security.JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<AuthDTOs.AuthResponse> register(@RequestBody AuthDTOs.RegisterRequest request) {
        User user = userService.registerUser(mapToEntity(request));
        String token = jwtUtils.generateToken(user.getEmail());
        return ResponseEntity.ok(AuthDTOs.AuthResponse.builder()
                .token(token)
                .user(mapToDTO(user))
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTOs.AuthResponse> login(@RequestBody AuthDTOs.LoginRequest request) {
        return userService.authenticate(request.getEmail(), request.getPassword())
                .map(user -> {
                    String token = jwtUtils.generateToken(user.getEmail());
                    return ResponseEntity.ok(AuthDTOs.AuthResponse.builder()
                            .token(token)
                            .user(mapToDTO(user))
                            .build());
                })
                .orElse(ResponseEntity.status(401).build());
    }

    // Helper mappers (in real app, use MapStruct)
    private User mapToEntity(AuthDTOs.RegisterRequest req) {
        return User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(req.getPassword())
                .phoneNumber(req.getPhoneNumber())
                .address(req.getAddress())
                .role(req.getRole() != null ? req.getRole() : com.shopsphere.api.domain.enums.UserRole.CUSTOMER)
                .gender(req.getGender())
                .dateOfBirth(req.getDateOfBirth())
                .build();
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .address(user.getAddress())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .build();
    }
}
