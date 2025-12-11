package com.insurance.controller;

import com.insurance.model.Policyholder;
import com.insurance.security.JwtTokenProvider;
import com.insurance.service.PolicyholderAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/policyholder")
public class PolicyholderAuthController {
    private static final Logger logger = LoggerFactory.getLogger(PolicyholderAuthController.class);

    private final PolicyholderAuthService policyholderAuthService;
    private final JwtTokenProvider jwtTokenProvider;

    public PolicyholderAuthController(PolicyholderAuthService policyholderAuthService, JwtTokenProvider jwtTokenProvider) {
        this.policyholderAuthService = policyholderAuthService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");
            
            if (email == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required"));
            }
            
            logger.debug("Policyholder login attempt for email: {}", email);
            
            Policyholder policyholder = policyholderAuthService.authenticatePolicyholder(email, password);
            
            // Generate JWT token
            String token = jwtTokenProvider.generatePolicyholderToken(policyholder);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "Login successful");
            response.put("policyholder", policyholder);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Policyholder login failed: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Policyholder policyholder) {
        try {
            logger.debug("Policyholder registration attempt for email: {}", policyholder.getEmail());
            
            Policyholder registered = policyholderAuthService.registerPolicyholder(policyholder);
            
            // Generate JWT token
            String token = jwtTokenProvider.generatePolicyholderToken(registered);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "Registration successful");
            response.put("policyholder", registered);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Policyholder registration failed: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }
} 