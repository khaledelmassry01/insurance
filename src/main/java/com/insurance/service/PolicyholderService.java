package com.insurance.service;

import com.insurance.model.Policyholder;
import com.insurance.repository.PolicyholderRepository;
import com.insurance.repository.PolicyholderRepository.PolicyholderWithStatusProjection;
import com.insurance.repository.InsuranceCoverageRepository;
import com.insurance.repository.ClaimRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@SuppressWarnings("null")
public class PolicyholderService {

    private static final Logger logger = LoggerFactory.getLogger(PolicyholderService.class);

    private final PolicyholderRepository policyholderRepository;
    private final InsuranceCoverageRepository insuranceCoverageRepository;
    private final ClaimRepository claimRepository;

    public PolicyholderService(PolicyholderRepository policyholderRepository, 
                               InsuranceCoverageRepository insuranceCoverageRepository,
                               ClaimRepository claimRepository) {
        this.policyholderRepository = policyholderRepository;
        this.insuranceCoverageRepository = insuranceCoverageRepository;
        this.claimRepository = claimRepository;
    }

    public List<Policyholder> getAllPolicyholders() {
        logger.debug("Fetching all policyholders");
        return policyholderRepository.findAll();
    }

    public Policyholder getPolicyholder(Integer id) {
        logger.debug("Fetching policyholder with id: {}", id);
        return policyholderRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Policyholder not found with id: " + id
            ));
    }
    
    /**
     * Find a policyholder by email
     * @param email The email of the policyholder
     * @return Optional containing the policyholder if found
     */
    public java.util.Optional<Policyholder> findByEmail(String email) {
        logger.debug("Fetching policyholder with email: {}", email);
        return policyholderRepository.findByEmail(email);
    }

    public Policyholder savePolicyholder(Policyholder policyholder) {
        logger.debug("Creating new policyholder: {}", policyholder.getEmail());
        if (policyholderRepository.existsByEmail(policyholder.getEmail())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Email already registered: " + policyholder.getEmail()
            );
        }

        if (policyholder.getDependant() != null) {
            Integer dependantId = policyholder.getDependant().getPolicyholderId();
            if (!policyholderRepository.existsById(dependantId)) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Dependant ID does not exist: " + dependantId
                );
            }
        } else {
            policyholder.setDependant(null);
            policyholder.setDependantRelation(null);
        }

        return policyholderRepository.save(policyholder);
    }

    public Policyholder updatePolicyholder(Integer id, Policyholder details) {
        logger.debug("Updating policyholder with id: {}", id);
        Policyholder existing = getPolicyholder(id);

        if (!existing.getEmail().equals(details.getEmail())
                && policyholderRepository.existsByEmail(details.getEmail())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Email already registered: " + details.getEmail()
            );
        }

        if (details.getDependant() != null) {
            Integer dependantId = details.getDependant().getPolicyholderId();
            if (!policyholderRepository.existsById(dependantId)) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Dependant ID does not exist: " + dependantId
                );
            }
        } else {
            details.setDependant(null);
            details.setDependantRelation(null);
        }

        existing.setFirstName(details.getFirstName());
        existing.setLastName(details.getLastName());
        existing.setEmail(details.getEmail());
        existing.setPhoneNumber(details.getPhoneNumber());
        existing.setGender(details.getGender());
        existing.setBirthdate(details.getBirthdate());
        existing.setEnrollmentDate(details.getEnrollmentDate());
        existing.setState(details.getState());
        existing.setCity(details.getCity());
        existing.setStreet(details.getStreet());
        existing.setZipCode(details.getZipCode());
        existing.setDependant(details.getDependant());
        existing.setDependantRelation(details.getDependantRelation());

        return policyholderRepository.save(existing);
    }

    public void deletePolicyholder(Integer id) {
        logger.debug("Deleting policyholder with id: {}", id);
        Policyholder existing = getPolicyholder(id);
        
        // Build detailed error message about related data
        StringBuilder errorMessage = new StringBuilder();
        boolean hasRelatedData = false;
        
        // Check for insurance coverages (active, suspended, expired, cancelled, etc.)
        long totalCoverages = insuranceCoverageRepository.countByPolicyholder_PolicyholderId(id);
        if (totalCoverages > 0) {
            hasRelatedData = true;
            errorMessage.append("- ").append(totalCoverages)
                        .append(" insurance policy/policies\n");
        }
        
        // Check for claims (approved, pending, rejected, etc.)
        Long totalClaims = claimRepository.countByPolicyholderId(id);
        if (totalClaims != null && totalClaims > 0) {
            hasRelatedData = true;
            errorMessage.append("- ").append(totalClaims)
                        .append(" insurance claim(s)\n");
        }
        
        // If policyholder has any related data, prevent deletion
        if (hasRelatedData) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Cannot delete policyholder. They have the following data in the system:\n" +
                errorMessage.toString() +
                "\nPolicyholders with insurance history cannot be deleted as all data is interconnected " +
                "and must be preserved for legal and auditing purposes. You can only delete policyholders " +
                "who have never had any insurance policies or claims with us."
            );
        }
        
        // Only delete if policyholder has NO related data at all
        policyholderRepository.delete(existing);
        logger.info("Policyholder with id {} deleted successfully (had no insurance history)", id);
    }

    public List<PolicyholderWithStatusProjection> getAllPolicyholdersWithStatus() {
        return policyholderRepository.findAllWithLatestStatus();
    }

    public List<PolicyholderWithStatusProjection> getAllPolicyholdersWithStatusByAgent(Integer agentId) {
        return policyholderRepository.findAllWithLatestStatusByAgentId(agentId);
    }

    public boolean isPolicyholderAssignedToAgent(Integer policyholderId, Integer agentId) {
        return insuranceCoverageRepository.existsByPolicyholder_PolicyholderIdAndAgent_AgentId(policyholderId, agentId);
    }

    public List<Policyholder> getAllPolicyholdersByAgent(Integer agentId) {
        return policyholderRepository.findAllByAgentId(agentId);
    }
}
