package com.insurance.service;

import com.insurance.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> authenticate(String email, String password) {
        return agentRepository.findByEmail(email)
                .filter(agent -> passwordEncoder.matches(password, agent.getPassword()))
                .map(agent -> ResponseEntity.ok().build())
                .orElse(ResponseEntity.badRequest().body("Invalid credentials"));
    }
} 