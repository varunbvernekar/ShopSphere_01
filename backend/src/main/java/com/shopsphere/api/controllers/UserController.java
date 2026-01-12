package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.requestDTO.UserUpdateRequest;
import com.shopsphere.api.dto.responseDTO.UserResponse;
import com.shopsphere.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
            @RequestBody UserUpdateRequest userUpdateRequest) {
        try {
            return ResponseEntity.ok(userService.updateUser(id, userUpdateRequest));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
