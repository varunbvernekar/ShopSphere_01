package com.shopsphere.api.services;

import com.shopsphere.api.dto.requestDTO.RegisterRequest;
import com.shopsphere.api.dto.requestDTO.UserUpdateRequest;
import com.shopsphere.api.dto.responseDTO.UserResponse;
import com.shopsphere.api.entity.User;
import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);

    UserResponse registerUser(User user); // Controller converts Request -> Entity for now, or change?
    // Let's keep consistency with earlier step where controller mapped.
    // Actually, `registerUser` in Controller currently maps DTO->Entity.
    // Let's stick to that for now to minimize churn, OR use RegisterRequest here?
    // Interface in step 123 was 'UserDTO registerUser(User user)'.

    Optional<User> authenticate(String email, String password);

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UserUpdateRequest updateRequest);
}
