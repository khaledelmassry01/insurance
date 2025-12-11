package com.insurance.controller;

import com.insurance.model.InsuranceCoverage;
import com.insurance.model.Policyholder;
import com.insurance.security.JwtTokenProvider;
import com.insurance.service.PolicyService;
import com.insurance.service.PolicyholderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for policyholder profile and policy management.
 * This controller handles requests from the policyholder portal.
 */
@RestController
@RequestMapping("/api/policyholder")
public class PolicyholderProfileController {
    private static final Logger logger = LoggerFactory.getLogger(PolicyholderProfileController.class);

    private final PolicyholderService policyholderService;
    private final PolicyService policyService;
    private final JwtTokenProvider jwtTokenProvider;

    public PolicyholderProfileController(
            PolicyholderService policyholderService,
            PolicyService policyService,
            JwtTokenProvider jwtTokenProvider) {
        this.policyholderService = policyholderService;
        this.policyService = policyService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Get the profile information of the authenticated policyholder.
     * 
     * @param token JWT token from the Authorization header
     * @return Policyholder profile data
     */
    @GetMapping("/profile")
    public ResponseEntity<Policyholder> getProfile(
            @RequestHeader("Authorization") String token) {
        try {
            // Extract token from Bearer prefix
            token = token.replace("Bearer ", "");
            
            // Validate token and check if it's a policyholder token
            if (!jwtTokenProvider.validateToken(token) || !jwtTokenProvider.isPolicyholderToken(token)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            }
            
            // Get policyholder ID from token
            int policyholderId = jwtTokenProvider.getPolicyholderIdFromToken(token);
            
            // Get policyholder data
            Policyholder policyholder = policyholderService.getPolicyholder(policyholderId);
            
            return ResponseEntity.ok(policyholder);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving policyholder profile: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving profile");
        }
    }

    /**
     * Get all policies for the authenticated policyholder.
     * 
     * @param token JWT token from the Authorization header
     * @return List of insurance coverages
     */
    @GetMapping("/policies")
    public ResponseEntity<List<InsuranceCoverage>> getPolicies(
            @RequestHeader("Authorization") String token) {
        try {
            // Extract token from Bearer prefix
            token = token.replace("Bearer ", "");
            
            // Validate token and check if it's a policyholder token
            if (!jwtTokenProvider.validateToken(token) || !jwtTokenProvider.isPolicyholderToken(token)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            }
            
            // Get policyholder ID from token
            int policyholderId = jwtTokenProvider.getPolicyholderIdFromToken(token);
            
            // Get policies for the policyholder
            List<InsuranceCoverage> policies = policyService.getPoliciesByPolicyholder(policyholderId);
            
            return ResponseEntity.ok(policies);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving policyholder policies: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving policies");
        }
    }

    /**
     * Get a specific policy for the authenticated policyholder.
     * 
     * @param token JWT token from the Authorization header
     * @param policyId ID of the policy to retrieve
     * @return Insurance coverage details
     */
    @GetMapping("/policies/{policyId}")
    public ResponseEntity<InsuranceCoverage> getPolicy(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer policyId) {
        try {
            // Extract token from Bearer prefix
            token = token.replace("Bearer ", "");
            
            // Validate token and check if it's a policyholder token
            if (!jwtTokenProvider.validateToken(token) || !jwtTokenProvider.isPolicyholderToken(token)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            }
            
            // Get policyholder ID from token
            int policyholderId = jwtTokenProvider.getPolicyholderIdFromToken(token);
            
            // Get the policy
            InsuranceCoverage policy = policyService.getInsuranceCoverage(policyId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found"));
            
            // Check if the policy belongs to the authenticated policyholder
            if (policy.getPolicyholder().getPolicyholderId() != policyholderId) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have access to this policy");
            }
            
            return ResponseEntity.ok(policy);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving policy details: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving policy details");
        }
    }
}
