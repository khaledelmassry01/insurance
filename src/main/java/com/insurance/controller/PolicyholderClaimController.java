package com.insurance.controller;

import com.insurance.model.Claim;
import com.insurance.model.Policyholder;
import com.insurance.service.ClaimService;
import com.insurance.service.PolicyholderService;
import com.insurance.service.PolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling policyholder-specific claim operations
 */
@RestController
@RequestMapping("/api/policyholder/claims")
public class PolicyholderClaimController {
    
    private final ClaimService claimService;
    private final PolicyService policyService;
    private final PolicyholderService policyholderService;
    
    public PolicyholderClaimController(
            ClaimService claimService, 
            PolicyService policyService,
            PolicyholderService policyholderService) {
        this.claimService = claimService;
        this.policyService = policyService;
        this.policyholderService = policyholderService;
    }
    
    /**
     * Get all claims for the authenticated policyholder
     */
    @GetMapping
    public ResponseEntity<List<Claim>> getPolicyholderClaims(Authentication auth) {
        try {
            // Log the request
            System.out.println("Received request for policyholder claims");
            
            // Get policyholder ID from the authentication token
            String emailAddress = auth.getName();
            System.out.println("Authenticated email: " + emailAddress);
            
            Policyholder policyholder = policyholderService.findByEmail(emailAddress)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid policyholder"));
            
            System.out.println("Found policyholder with ID: " + policyholder.getPolicyholderId());
            
            // Get all claims for this policyholder's policies
            List<Claim> claims = claimService.findByPolicyholderId(policyholder.getPolicyholderId());
            
            System.out.println("Found " + claims.size() + " claims for policyholder");
            
            return ResponseEntity.ok(claims);
        } catch (Exception e) {
            // Log the detailed error
            System.err.println("Error in getPolicyholderClaims: " + e.getMessage());
            e.printStackTrace();
            
            // Re-throw the exception to maintain the original behavior
            throw e;
        }
    }
    
    /**
     * Get a specific claim by ID for the authenticated policyholder
     */
    @GetMapping("/{id}")
    public ResponseEntity<Claim> getPolicyholderClaimById(@PathVariable Integer id, Authentication auth) {
        // Get policyholder ID from the authentication token
        String email = auth.getName();
        Policyholder policyholder = policyholderService.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid policyholder"));
        
        // Get the claim and verify it belongs to this policyholder
        Claim claim = claimService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found"));
        
        // Verify the claim belongs to one of the policyholder's policies
        if (!claimService.isClaimOwnedByPolicyholder(claim.getClaimId(), policyholder.getPolicyholderId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Claim does not belong to this policyholder");
        }
        
        return ResponseEntity.ok(claim);
    }
    
    /**
     * Submit a new claim for the authenticated policyholder
     */
    @PostMapping
    public ResponseEntity<Claim> submitClaim(@RequestBody Map<String, Object> claimData, Authentication auth) {
        // Get policyholder ID from the authentication token
        String email = auth.getName();
        Policyholder policyholder = policyholderService.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid policyholder"));
        
        // Extract claim data
        Integer policyId = Integer.valueOf(claimData.get("policyId").toString());
        String description = (String) claimData.get("description");
        Double amount = Double.valueOf(claimData.get("amount").toString());
        
        // Parse these values but store them in the ClaimDetails object that will be passed to the service
        // This way we're acknowledging we received them but delegating their processing to the service layer
        String incidentDateStr = (String) claimData.get("incidentDate");
        LocalDate incidentDate = LocalDate.parse(incidentDateStr);
        String claimType = (String) claimData.getOrDefault("claimType", "General");
        
        // Create a map with all claim details to pass to the service
        Map<String, Object> claimDetails = new HashMap<>();
        claimDetails.put("policyId", policyId);
        claimDetails.put("incidentDate", incidentDate);
        claimDetails.put("claimType", claimType);
        
        // Verify the policy belongs to this policyholder
        if (!policyService.isPolicyOwnedByPolicyholder(policyId, policyholder.getPolicyholderId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Policy does not belong to this policyholder");
        }
        
        // Create and save the claim
        Claim claim = new Claim();
        claim.setPolicyholder(policyholder);
        claim.setIncidentDescription(description); // Using the correct method name
        claim.setAmountClaimed(new java.math.BigDecimal(amount));
        
        // Set the insurance coverage and other required fields
        claimDetails.put("policyholderId", policyholder.getPolicyholderId());
        claimDetails.put("description", description);
        claimDetails.put("amount", amount);
        
        // The service layer will handle setting the insurance coverage based on policy ID
        // and adding the initial claim status
        Claim savedClaim = claimService.createClaimWithDetails(claim, claimDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClaim);
    }
}
