package com.wssoftwares.ikutibnb_backend.config;

import com.wssoftwares.ikutibnb_backend.users.entity.Role;
import com.wssoftwares.ikutibnb_backend.users.entity.User;
import com.wssoftwares.ikutibnb_backend.users.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeeder {

    @Bean
    @Order(2) // Run after schema creation
    public CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                if (userRepository.findByEmail("dalalimsomi03@gmail.com").isEmpty()) {
                    User admin = new User();
                    admin.setEmail("dalalimsomi03@gmail.com");
                    admin.setPassword(passwordEncoder.encode("Dalali@03"));
                    admin.setFullname("Dalali Msomi");
                    admin.setRole(Role.ADMIN);
                    userRepository.save(admin);
                    System.out.println("Admin user created successfully!");
                }
            } catch (Exception e) {
                System.err.println("Error seeding admin: " + e.getMessage());
            }
        };
    }
}