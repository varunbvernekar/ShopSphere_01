package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.UserDTO;
import com.shopsphere.api.entity.User;
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
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(mapToDTO(user));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            // Map DTO to entity for update (partial map)
            User updateRequest = User.builder()
                .name(userDTO.getName())
                .phoneNumber(userDTO.getPhoneNumber())
                .address(userDTO.getAddress())
                .gender(userDTO.getGender())
                .dateOfBirth(userDTO.getDateOfBirth())
                .build();
            // Note: Password update usually handled separately or requires explicit field in DTO
            
            User updated = userService.updateUser(id, updateRequest);
            return ResponseEntity.ok(mapToDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
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
