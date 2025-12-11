package com.insurance.service;

import com.insurance.model.Claim;
import com.insurance.model.ClaimStatus;
import com.insurance.model.ClaimStatusDetails;
import com.insurance.model.InsuranceCoverage;
import com.insurance.model.Policy;
import com.insurance.repository.ClaimRepository;
import com.insurance.repository.ClaimStatusDetailsRepository;
import com.insurance.repository.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@SuppressWarnings("null")
public class ClaimService {
    
    private final ClaimRepository claimRepository;
    private final ClaimStatusDetailsRepository statusDetailsRepository;
    private final PolicyRepository policyRepository;
    
    public ClaimService(ClaimRepository claimRepository,
                        ClaimStatusDetailsRepository statusDetailsRepository,
                        PolicyRepository policyRepository) {
        this.claimRepository = claimRepository;
        this.statusDetailsRepository = statusDetailsRepository;
        this.policyRepository = policyRepository;
    }
    
    public Claim createClaim(Claim claim) {
        if (claim.getClaimId() != null && claimRepository.existsById(claim.getClaimId())) {
            throw new RuntimeException("Claim already exists");
        }
        return claimRepository.save(claim);
    }
    
    /**
     * Create a claim with detailed information
     * @param claim The basic claim object
     * @param details Map containing additional details like policyId, incidentDate, etc.
     * @return The saved claim
     */
    public Claim createClaimWithDetails(Claim claim, Map<String, Object> details) {
        if (claim.getClaimId() != null && claimRepository.existsById(claim.getClaimId())) {
            throw new RuntimeException("Claim already exists");
        }
        
        // Extract details
        Integer policyId = (Integer) details.get("policyId");
        
        // Set insurance coverage based on policy ID
        // This assumes you have a method to get insurance coverage by policy ID
        // You may need to adjust this based on your actual data model
        Policy policy = policyRepository.findById(policyId)
            .orElseThrow(() -> new RuntimeException("Policy not found with id: " + policyId));
        
        // Get the insurance coverage for this policy
        List<InsuranceCoverage> coverages = policy.getInsuranceCoverages();
        if (coverages == null || coverages.isEmpty()) {
            throw new RuntimeException("No insurance coverage found for policy: " + policyId);
        }
        
        // Use the first coverage (you might need more complex logic depending on your requirements)
        InsuranceCoverage coverage = coverages.get(0);
        claim.setInsuranceCoverage(coverage);
        
        // Set agent from the insurance coverage
        claim.setAgent(coverage.getAgent());
        
        // Save the claim
        Claim savedClaim = claimRepository.save(claim);
        
        // Create initial claim status if needed
        if (details.containsKey("claimType")) {
            // Create a pending status
            ClaimStatusDetails pendingStatus = statusDetailsRepository.findByStatusName("Pending")
                .orElseThrow(() -> new RuntimeException("Pending status not found"));
            
            ClaimStatus initialStatus = new ClaimStatus(savedClaim, pendingStatus, LocalDate.now());
            savedClaim.getClaimStatuses().add(initialStatus);
            
            // Save again with the status
            savedClaim = claimRepository.save(savedClaim);
        }
        
        // Populate the insuranceId field (which is the policy ID)
        if (savedClaim.getInsuranceCoverage() != null) {
            savedClaim.setInsuranceId(savedClaim.getInsuranceCoverage().getInsuranceId());
        }
        
        return savedClaim;
    }
    
    public Optional<Claim> findById(Integer id) {
        Optional<Claim> claimOpt = claimRepository.findById(id);
        
        // Populate the insuranceId field if the claim exists
        if (claimOpt.isPresent()) {
            Claim claim = claimOpt.get();
            if (claim.getInsuranceCoverage() != null) {
                claim.setInsuranceId(claim.getInsuranceCoverage().getInsuranceId());
            }
        }
        
        return claimOpt;
    }
    
    public List<Claim> findByPolicyId(Integer policyId) {
        // calls  @Query findByPolicyId(...)
        return claimRepository.findByPolicyId(policyId);
    }
    
    public List<Claim> findByStatusId(Integer statusId) {
        // navigates through claimStatuses → statusDetails.statusId
        return claimRepository.findByClaimStatuses_StatusDetails_StatusId(statusId);
    }
    
    /**
     * Find all claims for a policyholder's policies
     * @param policyholderId The ID of the policyholder
     * @return List of claims for the policyholder's policies
     */
    public List<Claim> findByPolicyholderId(Integer policyholderId) {
        try {
            System.out.println("ClaimService: Finding claims for policyholder ID: " + policyholderId);
            List<Claim> claims = claimRepository.findByPolicyPolicyholderId(policyholderId);
            System.out.println("ClaimService: Found " + (claims != null ? claims.size() : 0) + " claims");
            
            // Populate the insuranceId field (which is the policy ID) for each claim
            if (claims != null) {
                for (Claim claim : claims) {
                    if (claim.getInsuranceCoverage() != null) {
                        // Set insuranceId (which is the policy ID)
                        claim.setInsuranceId(claim.getInsuranceCoverage().getInsuranceId());
                    }
                }
            }
            
            return claims;
        } catch (Exception e) {
            System.err.println("ClaimService: Error finding claims for policyholder ID: " + policyholderId);
            System.err.println("ClaimService: Error message: " + e.getMessage());
            e.printStackTrace();
            // Return an empty list instead of throwing an exception
            // This ensures the API doesn't fail even if there's a database issue
            return new java.util.ArrayList<>();
        }
    }
    
    /**
     * Check if a claim belongs to a policyholder
     * @param claimId The ID of the claim
     * @param policyholderId The ID of the policyholder
     * @return true if the claim belongs to the policyholder, false otherwise
     */
    public boolean isClaimOwnedByPolicyholder(Integer claimId, Integer policyholderId) {
        return claimRepository.existsByClaimIdAndPolicyPolicyholderId(claimId, policyholderId);
    }
    
    public Claim updateClaimStatus(Integer id, Integer statusId) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + id));
        
        ClaimStatusDetails newStatus = statusDetailsRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found with id: " + statusId));
        
        ClaimStatus claimStatus = new ClaimStatus(claim, newStatus, LocalDate.now());
        claim.getClaimStatuses().add(claimStatus);
        return claimRepository.save(claim);
    }
    
    public List<Claim> getAllClaimsForAdmin() {
        List<Claim> claims = claimRepository.findAllForAdmin();
        // Set transient insuranceId field for each claim
        for (Claim claim : claims) {
            if (claim.getInsuranceCoverage() != null) {
                claim.setInsuranceId(claim.getInsuranceCoverage().getInsuranceId());
            }
        }
        return claims;
    }
    
    public List<Claim> getAllClaimsForAgent(Integer agentId) {
        List<Claim> claims = claimRepository.findAllByAgentId(agentId);
        // Set transient insuranceId field for each claim
        for (Claim claim : claims) {
            if (claim.getInsuranceCoverage() != null) {
                claim.setInsuranceId(claim.getInsuranceCoverage().getInsuranceId());
            }
        }
        return claims;
    }
    
    public List<Claim> findByPolicyIdForAdmin(Integer policyId) {
        List<Claim> claims = claimRepository.findByPolicyId(policyId);
        // Set transient insuranceId field for each claim
        for (Claim claim : claims) {
            if (claim.getInsuranceCoverage() != null) {
                claim.setInsuranceId(claim.getInsuranceCoverage().getInsuranceId());
            }
        }
        return claims;
    }
    
    public List<Claim> findByPolicyIdForAgent(Integer policyId, Integer agentId) {
        // Only claims for this policy and this agent
        List<Claim> claims = claimRepository.findByPolicyId(policyId).stream()
            .filter(c -> c.getAgent().getAgentId().equals(agentId))
            .toList();
        // Set transient insuranceId field for each claim
        for (Claim claim : claims) {
            if (claim.getInsuranceCoverage() != null) {
                claim.setInsuranceId(claim.getInsuranceCoverage().getInsuranceId());
            }
        }
        return claims;
    }
    
    public List<Claim> findByStatusIdForAdmin(Integer statusId) {
        List<Claim> claims = claimRepository.findByClaimStatuses_StatusDetails_StatusId(statusId);
        // Set transient insuranceId field for each claim
        for (Claim claim : claims) {
            if (claim.getInsuranceCoverage() != null) {
                claim.setInsuranceId(claim.getInsuranceCoverage().getInsuranceId());
            }
        }
        return claims;
    }
    
    public List<Claim> findByStatusIdForAgent(Integer statusId, Integer agentId) {
        List<Claim> claims = claimRepository.findByClaimStatuses_StatusDetails_StatusId(statusId).stream()
            .filter(c -> c.getAgent().getAgentId().equals(agentId))
            .toList();
        // Set transient insuranceId field for each claim
        for (Claim claim : claims) {
            if (claim.getInsuranceCoverage() != null) {
                claim.setInsuranceId(claim.getInsuranceCoverage().getInsuranceId());
            }
        }
        return claims;
    }
}
