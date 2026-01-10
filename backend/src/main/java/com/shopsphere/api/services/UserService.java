package com.shopsphere.api.services;

import com.shopsphere.api.entity.User;
import com.shopsphere.api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already taken");
        }
        // In a real app we'd hash the password here. For this demo we'll store as
        // plain/as-is since frontend was doing that with json-server
        return userRepository.save(user);
    }

    public Optional<User> authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(u -> u.getPassword().equals(password));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUser(Long id, User updatedUser) {
        User existing = getUserById(id);

        // Update allowed fields
        existing.setName(updatedUser.getName());
        existing.setPhoneNumber(updatedUser.getPhoneNumber());
        existing.setAddress(updatedUser.getAddress());
        existing.setGender(updatedUser.getGender());
        existing.setDateOfBirth(updatedUser.getDateOfBirth());

        // Only update password if provided and not empty
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existing.setPassword(updatedUser.getPassword());
        }

        return userRepository.save(existing);
    }
}
