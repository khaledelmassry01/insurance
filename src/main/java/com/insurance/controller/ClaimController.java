package com.insurance.controller;

import com.insurance.model.Claim;
import com.insurance.model.Agent;
import com.insurance.repository.AgentRepository;
import com.insurance.security.AgentUserDetails;
import com.insurance.service.ClaimService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {
    
    private final ClaimService claimService;
    private final AgentRepository agentRepository;
    
    public ClaimController(ClaimService claimService, AgentRepository agentRepository) {
        this.claimService = claimService;
        this.agentRepository = agentRepository;
    }
    
    private Integer getAgentIdFromAuth(Authentication auth) {
        Object principal = auth.getPrincipal();
        
        if (principal instanceof AgentUserDetails) {
            return ((AgentUserDetails) principal).getAgentId();
        }
        
        if (principal instanceof String) {
            return Integer.parseInt((String) principal);
        }
        
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            Optional<Agent> agentOpt = agentRepository.findByEmail(email);
            return agentOpt.map(Agent::getAgentId).orElse(null);
        }
        
        return null;
    }
    
    @PostMapping
    public ResponseEntity<Claim> createClaim(@RequestBody Claim claim) {
        Claim createdClaim = claimService.createClaim(claim);
        return ResponseEntity.ok(createdClaim);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Claim> getClaimById(@PathVariable Integer id) {
        return claimService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/policy/{policyId}")
    public ResponseEntity<List<Claim>> getClaimsByPolicy(@PathVariable Integer policyId, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMIN"));
        Integer agentId = getAgentIdFromAuth(auth);
        List<Claim> claims = isAdmin
            ? claimService.findByPolicyIdForAdmin(policyId)
            : claimService.findByPolicyIdForAgent(policyId, agentId);
        return ResponseEntity.ok(claims);
    }
    
    @GetMapping("/status/{statusId}")
    public ResponseEntity<List<Claim>> getClaimsByStatus(@PathVariable Integer statusId, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMIN"));
        Integer agentId = getAgentIdFromAuth(auth);
        List<Claim> claims = isAdmin
            ? claimService.findByStatusIdForAdmin(statusId)
            : claimService.findByStatusIdForAgent(statusId, agentId);
        return ResponseEntity.ok(claims);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Claim> updateClaimStatus(@PathVariable Integer id,
                                                   @RequestParam Integer statusId) {
        Claim updatedClaim = claimService.updateClaimStatus(id, statusId);
        return ResponseEntity.ok(updatedClaim);
    }
    
    @GetMapping
    public ResponseEntity<List<Claim>> getAllClaims(Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMIN"));
        Integer agentId = getAgentIdFromAuth(auth);
        List<Claim> list = isAdmin
            ? claimService.getAllClaimsForAdmin()
            : claimService.getAllClaimsForAgent(agentId);
        return ResponseEntity.ok(list);
    }
    
}
