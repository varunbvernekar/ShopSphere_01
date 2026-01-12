package com.shopsphere.api.services.impl;

import com.shopsphere.api.dto.requestDTO.UserUpdateRequest;
import com.shopsphere.api.dto.responseDTO.UserResponse;
import com.shopsphere.api.entity.User;
import com.shopsphere.api.repositories.UserRepository;
import com.shopsphere.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserResponse registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already taken");
        }
        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    @Override
    public Optional<User> authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(u -> u.getPassword().equals(password));
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserResponse.fromEntity(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest updateRequest) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Initial update logic manual since UserUpdateRequest -> User conversion wasn't
        // moved to DTO fully for updates
        if (updateRequest != null) {
            if (updateRequest.getName() != null)
                existing.setName(updateRequest.getName());
            if (updateRequest.getPhoneNumber() != null)
                existing.setPhoneNumber(updateRequest.getPhoneNumber());
            if (updateRequest.getAddress() != null)
                existing.setAddress(updateRequest.getAddress());
            if (updateRequest.getGender() != null)
                existing.setGender(updateRequest.getGender());
            if (updateRequest.getDateOfBirth() != null)
                existing.setDateOfBirth(updateRequest.getDateOfBirth());
        }

        User savedUser = userRepository.save(existing);
        return UserResponse.fromEntity(savedUser);
    }
}
