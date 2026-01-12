package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.requestDTO.LoginRequest;
import com.shopsphere.api.dto.requestDTO.RegisterRequest;
import com.shopsphere.api.dto.responseDTO.AuthResponse;
import com.shopsphere.api.dto.responseDTO.UserResponse;
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
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        User userEntity = mapToEntity(request);
        UserResponse createdUser = userService.registerUser(userEntity);

        String token = jwtUtils.generateToken(createdUser.getEmail());
        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .user(createdUser)
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return userService.authenticate(request.getEmail(), request.getPassword())
                .map(user -> {
                    String token = jwtUtils.generateToken(user.getEmail());
                    return ResponseEntity.ok(AuthResponse.builder()
                            .token(token)
                            .user(UserResponse.fromEntity(user))
                            .build());
                })
                .orElse(ResponseEntity.status(401).build());
    }

    private User mapToEntity(RegisterRequest req) {
        return User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(req.getPassword())
                .phoneNumber(req.getPhoneNumber())
                .address(req.getAddress())
                .role(com.shopsphere.api.enums.UserRole.CUSTOMER)
                .gender(req.getGender())
                .dateOfBirth(req.getDateOfBirth())
                .build();
    }
}
