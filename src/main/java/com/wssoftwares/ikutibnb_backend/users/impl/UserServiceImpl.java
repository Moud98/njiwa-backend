package com.wssoftwares.ikutibnb_backend.users.impl;

import com.wssoftwares.ikutibnb_backend.users.UserRepository;
import com.wssoftwares.ikutibnb_backend.users.UserService;
import com.wssoftwares.ikutibnb_backend.users.dto.ChangePassword;
import com.wssoftwares.ikutibnb_backend.users.dto.CreateUser;
import com.wssoftwares.ikutibnb_backend.users.dto.UpdateUser;
import com.wssoftwares.ikutibnb_backend.auth.service.JwtService;
import com.wssoftwares.ikutibnb_backend.exception.EmailAlreadyExistsException;
import com.wssoftwares.ikutibnb_backend.house.ImageUtils;
import com.wssoftwares.ikutibnb_backend.rentRequest.entity.RentRequest;
import com.wssoftwares.ikutibnb_backend.config.AppConfig;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.wssoftwares.ikutibnb_backend.users.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import java.util.Collections;
import jakarta.persistence.EntityNotFoundException;
import com.wssoftwares.ikutibnb_backend.rentRequest.RentRequestRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RentRequestRepository rentRequestRepository;
    private final JwtService jwtService;
    private final AppConfig appConfig;
    private final ImageUtils imageUtils;

    public UserServiceImpl(UserRepository userRepository,
            RentRequestRepository rentRequestRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AppConfig appConfig,
            ImageUtils imageUtils) {
        this.userRepository = userRepository;
        this.rentRequestRepository = rentRequestRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.appConfig = appConfig;
        this.imageUtils = imageUtils;
    }

    private boolean isUrl(String value) {
        return value.startsWith("http://") || value.startsWith("https://");
    }

    @Override
    public User createUser(CreateUser dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already exists. Please use a different email.");
        }

        String hashedPassword = passwordEncoder.encode(dto.getPassword());

        User user = new User(
                null,
                dto.getFullname(),
                dto.getEmail(),
                hashedPassword,
                null,
                dto.getPhonenumber(),
                dto.getRole());

        User savedUser = userRepository.save(user);

        if (dto.getProfilepicture() != null && dto.getProfilepicture().startsWith("data:image/")) {
            try {
                String profilePictureUrl = imageUtils.saveProfilePicture(dto.getProfilepicture(), savedUser.getId());
                savedUser.setProfilepicture(profilePictureUrl);
                savedUser = userRepository.save(savedUser);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save profile picture", e);
            }
        }

        return savedUser;
    }

    @Override
    public Page<User> getAllUsers(int page, int size, String role, String fullname) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Specification<User> spec = Specification.where(null);

        // Filter by role
        if (role != null && !role.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("role"), Role.valueOf(role.toUpperCase())));
        }

        // Filter by fullname (case-insensitive, contains)
        if (fullname != null && !fullname.trim().isEmpty()) {
            String fullnamePattern = "%" + fullname.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("fullname")), fullnamePattern));
        }

        return userRepository.findAll(spec, pageable);
    }

    @Override
    public List<User> getAllHosts() {
        return userRepository.findByRole(Role.HOST);
    }

    @Override
    public List<User> getAllAgents() {
        return userRepository.findByRole(Role.AGENT);
    }

    @Override
    public List<User> getAllClients() {
        return userRepository.findByRole(Role.CLIENT);
    }

    @Override
    public Page<User> getClientsWhoRented(String fullname, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<User> clients = rentRequestRepository.findAll()
                .stream()
                .map(RentRequest::getClient)
                .filter(Objects::nonNull)
                .distinct()
                .filter(user -> fullname == null ||
                        user.getFullname().toLowerCase().contains(fullname.toLowerCase()))
                .toList();

        int start = (int) pageable.getOffset();
        if (start >= clients.size()) {
            // Return empty page if requested page is beyond last page
            return new PageImpl<>(Collections.emptyList(), pageable, clients.size());
        }

        int end = Math.min(start + pageable.getPageSize(), clients.size());
        List<User> pagedClients = clients.subList(start, end);

        return new PageImpl<>(pagedClients, pageable, clients.size());
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User updateUser(UUID id, UpdateUser dto, String reviewerEmail) {
        User user = getUserById(id);

        if (dto.getReview() != null && !dto.getReview().isEmpty()) {
            User reviewer = getUserByEmail(reviewerEmail);
            if (user.getId().equals(reviewer.getId())) {
                throw new IllegalArgumentException("You cannot review your own account");
            }
            List<com.wssoftwares.ikutibnb_backend.users.dto.Review> reviews = user.getReview() == null
                    ? new java.util.ArrayList<>() : new java.util.ArrayList<>(user.getReview());
            for (com.wssoftwares.ikutibnb_backend.users.dto.Review review : dto.getReview()) {
                if (review == null || review.getMessage() == null || review.getMessage().isBlank()) {
                    throw new IllegalArgumentException("Review message is required");
                }
                reviews.add(review.withName(reviewer.getFullname()));
            }
            user.setReview(reviews);
        }

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new EmailAlreadyExistsException(
                        "Email already exists. Please use a different email.");
            }
            user.setEmail(dto.getEmail());
        }

        if (dto.getFullname() != null) {
            user.setFullname(dto.getFullname());
        }

        if (dto.getPhonenumber() != null) {
            user.setPhonenumber(dto.getPhonenumber());
        }

        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }

        if (dto.getProfilepicture() != null) {
            String profilePicture = dto.getProfilepicture();

            try {
                // Check if the value is a URL
                if (isUrl(profilePicture)) {
                    // Save URL directly
                    user.setProfilepicture(profilePicture);
                } else {
                    // Assume Base64 image and save as before
                    String profilePictureUrl = imageUtils.saveProfilePicture(profilePicture, user.getId());
                    user.setProfilepicture(profilePictureUrl);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to save profile picture", e);
            }
        }

        return userRepository.save(user);
    }

    @Override
    public User changePassword(UUID id, ChangePassword dto) {
        User user = getUserById(id);

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public User getUserFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String email = jwtService.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for token"));
    }
}
