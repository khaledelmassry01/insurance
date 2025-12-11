package com.insurance.service;

import com.insurance.model.Policyholder;
import com.insurance.repository.PolicyholderRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
    
    private final PolicyholderRepository policyholderRepository;
    
    public SecurityService(PolicyholderRepository policyholderRepository) {
        this.policyholderRepository = policyholderRepository;
    }
    
    public boolean isPolicyholder(Integer policyholderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        String email = authentication.getName();
        return policyholderRepository.findByEmail(email)
            .map(Policyholder::getPolicyholderId)
            .map(id -> id.equals(policyholderId))
            .orElse(false);
    }

    public Integer currentAgentId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Integer) auth.getDetails();
    }
} 