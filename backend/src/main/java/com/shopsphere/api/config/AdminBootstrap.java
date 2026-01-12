package com.shopsphere.api.config;

import com.shopsphere.api.enums.UserRole;
import com.shopsphere.api.entity.User;
import com.shopsphere.api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@shopsphere.com").isEmpty()) {
            User admin = User.builder()
                    .name("Main Admin")
                    .email("admin@shopsphere.com")
                    .password("admin123") // NoOpPasswordEncoder will treat this as-is
                    .role(UserRole.ADMIN)
                    .phoneNumber("0000000000")
                    .build();
            userRepository.save(admin);
            System.out.println("Default Admin account created: admin@shopsphere.com / admin123");
        }
    }
}
