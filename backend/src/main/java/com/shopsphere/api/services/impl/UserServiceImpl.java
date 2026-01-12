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
@lombok.extern.slf4j.Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public UserResponse registerUser(User user) {
        log.info("Attempting to register user with email: {}", user.getEmail());
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("Registration failed: Email {} already taken", user.getEmail());
            throw new RuntimeException("Email already taken");
        }
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        return UserResponse.fromEntity(savedUser);
    }

    @Override
    public Optional<User> authenticate(String email, String password) {
        log.debug("Authenticating user: {}", email);
        Optional<User> user = userRepository.findByEmail(email)
                .filter(u -> u.getPassword().equals(password));
        if (user.isPresent()) {
            log.info("Authentication successful for email: {}", email);
        } else {
            log.warn("Authentication failed for email: {}", email);
        }
        return user;
    }

    @Override
    public UserResponse getUserById(Long id) {
        log.debug("Fetching user profile for ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new RuntimeException("User not found");
                });
        return UserResponse.fromEntity(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest updateRequest) {
        log.info("Updating user profile for ID: {}", id);
        User existing = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found for update with ID: {}", id);
                    return new RuntimeException("User not found");
                });

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
        log.info("User profile updated successfully for ID: {}", id);
        return UserResponse.fromEntity(savedUser);
    }
}
