package com.insurance.service;

import com.insurance.model.Policyholder;
import com.insurance.repository.PolicyholderRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PolicyholderAuthService {

    private final PolicyholderRepository policyholderRepository;
    private final PasswordEncoder passwordEncoder;

    public PolicyholderAuthService(PolicyholderRepository policyholderRepository, PasswordEncoder passwordEncoder) {
        this.policyholderRepository = policyholderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Policyholder authenticatePolicyholder(String email, String password) {
        Policyholder policyholder = policyholderRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(password, policyholder.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return policyholder;
    }

    public Policyholder registerPolicyholder(Policyholder policyholder) {
        if (policyholderRepository.existsByEmail(policyholder.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        policyholder.setPassword(passwordEncoder.encode(policyholder.getPassword()));
        return policyholderRepository.save(policyholder);
    }
}
