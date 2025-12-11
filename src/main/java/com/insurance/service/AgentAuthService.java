package com.insurance.service;

import com.insurance.model.Agent;
import com.insurance.repository.AgentRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AgentAuthService {

    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;

    public AgentAuthService(AgentRepository agentRepository, PasswordEncoder passwordEncoder) {
        this.agentRepository = agentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Agent authenticateAgent(String email, String password) {
        Agent agent = agentRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(password, agent.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return agent;
    }
}
