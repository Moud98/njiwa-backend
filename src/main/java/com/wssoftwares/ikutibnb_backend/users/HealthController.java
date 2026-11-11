package com.wssoftwares.ikutibnb_backend.users;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user/api/health")
public class HealthController {

    private final UserRepository userRepository;

    public HealthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");

        try {
            userRepository.count(); 
            health.put("database", "UP");
        } catch (Exception e) {
            health.put("database", "DOWN");
        }

        health.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(health);
    }
}

