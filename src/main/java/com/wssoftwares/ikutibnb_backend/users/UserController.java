package com.wssoftwares.ikutibnb_backend.users;

import com.wssoftwares.ikutibnb_backend.exception.EmailAlreadyExistsException;
import com.wssoftwares.ikutibnb_backend.users.dto.ChangePassword;
import com.wssoftwares.ikutibnb_backend.users.dto.CreateUser;
import com.wssoftwares.ikutibnb_backend.users.dto.UpdateUser;
import com.wssoftwares.ikutibnb_backend.users.dto.UserResponse;
import com.wssoftwares.ikutibnb_backend.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUser dto) {
        try {
            User createdUser = userService.createUser(dto);

            Map<String, Object> response = new HashMap<>();
            response.put("id", createdUser.getId());
            response.put("fullname", createdUser.getFullname());
            response.put("email", createdUser.getEmail());
            response.put("profilepicture", createdUser.getProfilepicture());
            response.put("phonenumber", createdUser.getPhonenumber());
            response.put("role", createdUser.getRole());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (EmailAlreadyExistsException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String fullname) {

        Page<User> usersPage = userService.getAllUsers(page, size, role, fullname);

        List<UserResponse> userResponses = usersPage.getContent()
                .stream()
                .map(UserResponse::new)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("users", userResponses);
        response.put("totalPages", usersPage.getTotalPages());
        response.put("totalElements", usersPage.getTotalElements());
        response.put("currentPage", usersPage.getNumber() + 1);
        response.put("pageSize", usersPage.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/hosts")
    public List<UserResponse> getAllHosts() {
        return userService.getAllHosts()
                .stream()
                .map(UserResponse::new)
                .toList();
    }

    @GetMapping("/agents")
    public List<UserResponse> getAllAgents() {
        return userService.getAllAgents()
                .stream()
                .map(UserResponse::new)
                .toList();
    }

    @GetMapping("/clients")
    public List<UserResponse> getAllClients() {
        return userService.getAllClients()
                .stream()
                .map(UserResponse::new)
                .toList();
    }

    @GetMapping("/renters")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getRentingClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) String fullname) {

        Page<User> clientsPage = userService.getClientsWhoRented(fullname, page, size);

        List<UserResponse> resp = clientsPage.getContent()
                .stream()
                .map(UserResponse::new)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("rentingClients", resp);
        response.put("totalPages", clientsPage.getTotalPages());
        response.put("totalElements", clientsPage.getTotalElements());
        response.put("currentPage", clientsPage.getNumber() + 1);
        response.put("pageSize", clientsPage.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(new UserResponse(user));
        } catch (RuntimeException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage()); // "User not found"
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'HOST', 'AGENT', 'ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody UpdateUser dto,
            @AuthenticationPrincipal UserDetails authenticatedUser) {
        try {
            User updatedUser = userService.updateUser(id, dto, authenticatedUser.getUsername());
            return ResponseEntity.ok(new UserResponse(updatedUser));
        } catch (EmailAlreadyExistsException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PatchMapping("/passwd/{id}")
    public ResponseEntity<?> changePassword(@PathVariable UUID id, @RequestBody ChangePassword dto) {
        try {
            userService.changePassword(id, dto);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteUser(
            @PathVariable UUID id) {

        try {
            userService.deleteUser(id);

            Map<String, Object> response = new HashMap<>();
            response.put("id", id);
            response.put("message", "User deleted successfully");

            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("id", id);
            response.put("message", "User not found");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
