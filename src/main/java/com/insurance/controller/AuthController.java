package com.insurance.controller;

import com.insurance.model.Agent;
import com.insurance.security.JwtTokenProvider;
import com.insurance.service.AgentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AgentService agentService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          AgentService agentService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider   = jwtTokenProvider;
        this.agentService       = agentService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                credentials.get("email"),
                credentials.get("password")
            )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. جلب بيانات الوكيل (لنستخدم agentId لاحقاً في التوكن)
        Agent agent = agentService.getAgentByEmail(credentials.get("email"));

        // 3. إصدار التوكن (generateToken يأخذ agentId من Principal)
        String token = jwtTokenProvider.generateToken(authentication);

        // 4. تجهيز الرد
        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("agent", Map.of(
            "agentId",       agent.getAgentId(),
            "licenceNumber", agent.getLicenceNumber(),
            "firstName",     agent.getFirstName(),
            "lastName",      agent.getLastName(),
            "email",         agent.getEmail(),
            "role",          agent.getRole() != null
                                ? agent.getRole().toUpperCase()
                                : "AGENT",
            "isAdmin",       agent.getRole() != null
                                && agent.getRole().equalsIgnoreCase("ADMIN")
        ));

        return ResponseEntity.ok(body);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Agent agent) {
        try {
            // Set default role if not provided
            if (agent.getRole() == null || agent.getRole().isEmpty()) {
                agent.setRole("AGENT");
            }
            
            // Check if email already exists
            try {
                Agent existingAgent = agentService.getAgentByEmail(agent.getEmail());
                if (existingAgent != null) {
                    return ResponseEntity.badRequest()
                        .body(Map.of("message", "Email already registered"));
                }
            } catch (Exception e) {
                // Email doesn't exist, proceed with registration
            }
            
            // Save the new agent
            Agent savedAgent = agentService.createAgent(agent);
            
            return ResponseEntity.ok(Map.of(
                "message", "Registration successful",
                "agent", Map.of(
                    "agentId", savedAgent.getAgentId(),
                    "email", savedAgent.getEmail(),
                    "firstName", savedAgent.getFirstName(),
                    "lastName", savedAgent.getLastName()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(401).body("Invalid token");
    }
}
