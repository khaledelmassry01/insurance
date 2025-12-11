package com.insurance.controller;

import com.insurance.model.Agent;
import com.insurance.model.Policyholder;
import com.insurance.repository.AgentRepository;
import com.insurance.service.PolicyholderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.insurance.repository.PolicyholderRepository.PolicyholderWithStatusProjection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@RestController
@RequestMapping("/api/policyholders")
public class PolicyholderController {
    private static final Logger logger = LoggerFactory.getLogger(PolicyholderController.class);
    private final PolicyholderService policyholderService;
    private final AgentRepository agentRepository;

    public PolicyholderController(PolicyholderService policyholderService, AgentRepository agentRepository) {
        this.policyholderService = policyholderService;
        this.agentRepository = agentRepository;
    }

    /**
     * Get all policyholders (ADMIN only)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<?> getAllPolicyholders(Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (isAdmin) {
            List<PolicyholderWithStatusProjection> list = policyholderService.getAllPolicyholdersWithStatus();
            return ResponseEntity.ok(list);
        } else {
            List<PolicyholderWithStatusProjection> list = policyholderService.getAllPolicyholdersWithStatusByAgent(getAgentIdFromAuth(auth));
            return ResponseEntity.ok(list);
        }
    }

    /**
     * Get policyholder by id (ADMIN only, or AGENT if assigned)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<Policyholder> getPolicyholder(@PathVariable Integer id, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMIN"));

        Policyholder policyholder = policyholderService.getPolicyholder(id);

        if (!isAdmin) {
            // AGENT: Check if this policyholder is assigned to the agent
            Integer agentId = getAgentIdFromAuth(auth);
            if (!policyholderService.isPolicyholderAssignedToAgent(id, agentId)) {
                 throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Agent not authorized for this policyholder");
            }
        }
        // If ADMIN or assigned AGENT, return the policyholder
        return ResponseEntity.ok(policyholder);
    }

    /**
     * Create new policyholder (ADMIN only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Policyholder> createPolicyholder(
            @Valid @RequestBody Policyholder policyholder,
            Authentication auth) {
        logger.debug("Creating new policyholder requested by ADMIN: {}", policyholder.getEmail());
        try {
            return ResponseEntity.ok(policyholderService.savePolicyholder(policyholder));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Update policyholder (ADMIN only, or AGENT if assigned)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<Policyholder> updatePolicyholder(@PathVariable Integer id, @RequestBody Policyholder policyholder, Authentication auth) {
         boolean isAdmin = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!isAdmin) {
            // AGENT: Check if this policyholder is assigned to the agent before allowing update
            Integer agentId = getAgentIdFromAuth(auth);
            if (!policyholderService.isPolicyholderAssignedToAgent(id, agentId)) {
                 throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Agent not authorized to update this policyholder");
            }
            // Potentially add logic here to prevent AGENT from changing assignment-related fields if needed
        }

        Policyholder updated = policyholderService.updatePolicyholder(id, policyholder);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete policyholder (ADMIN only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePolicyholder(@PathVariable Integer id) {
        logger.debug("Deleting policyholder {} requested by ADMIN", id);
        policyholderService.deletePolicyholder(id);
        return ResponseEntity.noContent().build();
    }

    // Helper method to extract Agent ID from Authentication
    private Integer getAgentIdFromAuth(Authentication auth) {
        Object principal = auth.getPrincipal();
        
        // Case 1: If principal is AgentUserDetails, get agentId directly
        if (principal instanceof com.insurance.security.AgentUserDetails) {
            return ((com.insurance.security.AgentUserDetails) principal).getAgentId();
        }
        
        // Case 2: If principal is String, it's the agentId from JWT subject
        if (principal instanceof String) {
            try {
                return Integer.parseInt((String) principal);
            } catch (NumberFormatException e) {
                logger.error("Could not parse agentId from principal string: {}", principal);
            }
        }
        
        // Case 3: Fallback to finding by email (UserDetails)
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return agentRepository.findByEmail(email)
                .map(Agent::getAgentId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, 
                    "Agent not found for email: " + email
                ));
        }
        
        logger.error("Cannot determine agentId from principal of type: {}", 
            principal != null ? principal.getClass().getName() : "null");
        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "Could not determine agentId from authentication principal"
        );
    }
}
