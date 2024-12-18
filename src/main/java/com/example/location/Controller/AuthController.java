package com.example.location.Controller;
import com.example.location.Entity.UserEntity;
import com.example.location.Repository.UserRepository;
import com.example.location.Security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user or owner.
     * The request body should include username, password, and role (USER or OWNER).
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserEntity user) {
        // Check if the username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken.");
        }

        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    /**
     * Login endpoint to authenticate users and generate JWT tokens.
     * The request body should include username and password.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // Check if the user exists
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password."));

        // Validate the password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password.");
        }

        // Generate JWT token
        String token = jwtService.generateToken(user.getUsername(), user.getRole());

        return ResponseEntity.ok(Map.of("token", token));
    }
}

