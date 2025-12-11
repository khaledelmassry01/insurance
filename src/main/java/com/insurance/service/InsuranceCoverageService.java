package com.insurance.service;

import com.insurance.model.InsuranceCoverage;
import com.insurance.model.PolicyStatus;
import com.insurance.repository.InsuranceCoverageRepository;
import com.insurance.repository.PolicyStatusRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("null")
public class InsuranceCoverageService {

    private final InsuranceCoverageRepository insuranceCoverageRepository;
    private final PolicyStatusRepository policyStatusRepository;

    public InsuranceCoverageService(
            InsuranceCoverageRepository insuranceCoverageRepository,
            PolicyStatusRepository policyStatusRepository) {
        this.insuranceCoverageRepository = insuranceCoverageRepository;
        this.policyStatusRepository = policyStatusRepository;
    }

    /**
     * Retrieves all insurance coverages.
     */
    public List<InsuranceCoverage> getAllInsuranceCoverages() {
        return insuranceCoverageRepository.findAll();
    }

    /**
     * Retrieves a single insurance coverage by its ID.
     */
    public Optional<InsuranceCoverage> getInsuranceCoverageById(Integer id) {
        return insuranceCoverageRepository.findById(id);
    }

    /**
     * Creates a new insurance coverage.
     * The insuranceId is generated automatically by the database.
     */
    @Transactional
    public InsuranceCoverage createInsuranceCoverage(InsuranceCoverage insuranceCoverage) {
        return insuranceCoverageRepository.save(insuranceCoverage);
    }

    /**
     * Updates an existing insurance coverage.
     * Only mutable fields are updated; the ID remains unchanged.
     */
    @Transactional
    public InsuranceCoverage updateInsuranceCoverage(Integer id, InsuranceCoverage updatedCoverage) {
        return insuranceCoverageRepository.findById(id)
            .map(existing -> {
                existing.setPolicyholder(updatedCoverage.getPolicyholder());
                existing.setAgent(updatedCoverage.getAgent());
                existing.setPolicy(updatedCoverage.getPolicy());
                existing.setStatus(updatedCoverage.getStatus());
                existing.setExpirationDate(updatedCoverage.getExpirationDate());
                existing.setPremium(updatedCoverage.getPremium());
                existing.setCoverageAmount(updatedCoverage.getCoverageAmount());
                return insuranceCoverageRepository.save(existing);
            })
            .orElseThrow(() -> new RuntimeException("Insurance coverage not found with id: " + id));
    }

    /**
     * Deletes a coverage by its ID.
     */
    @Transactional
    public void deleteInsuranceCoverage(Integer id) {
        insuranceCoverageRepository.deleteById(id);
    }

    /**
     * Finds all coverages for a given policyholder.
     */
    public List<InsuranceCoverage> getInsuranceCoveragesByPolicyholderId(Integer policyholderId) {
        return insuranceCoverageRepository.findByPolicyholder_PolicyholderId(policyholderId);
    }

    /**
     * Finds all coverages for a given agent.
     */
    public List<InsuranceCoverage> getInsuranceCoveragesByAgentId(Integer agentId) {
        return insuranceCoverageRepository.findByAgent_AgentId(agentId);
    }

    /**
     * Finds all coverages for a given policy type.
     */
    public List<InsuranceCoverage> getInsuranceCoveragesByPolicyId(Integer policyId) {
        return insuranceCoverageRepository.findByPolicy_PolicyId(policyId);
    }

    /**
     * Finds all coverages matching a given status name.
     */
    public List<InsuranceCoverage> getInsuranceCoveragesByStatus(String statusName) {
        PolicyStatus status = policyStatusRepository.findByStatusName(statusName)
            .orElseThrow(() -> new RuntimeException("Policy status not found: " + statusName));
        return insuranceCoverageRepository.findByStatus(status);
    }

    /**
     * Finds currently active policies.
     */
    public List<InsuranceCoverage> getActivePolicies() {
        return insuranceCoverageRepository.findActivePolicies();
    }

    /**
     * Finds expired policies.
     */
    public List<InsuranceCoverage> getExpiredPolicies() {
        return insuranceCoverageRepository.findExpiredPolicies();
    }

    /**
     * Counts active policies for a given agent.
     */
    public Long countActivePoliciesByAgent(Integer agentId) {
        return insuranceCoverageRepository.countActivePoliciesByAgentId(agentId, "Active");
    }

    /**
     * Sums total premium for a given agent.
     */
    public BigDecimal calculateTotalPremiumByAgent(Integer agentId) {
        return insuranceCoverageRepository.calculateTotalPremiumByAgentId(agentId);
    }

    /**
     * Counts active policies for a given policyholder.
     */
    public Long countActivePoliciesByPolicyholder(Integer policyholderId) {
        return insuranceCoverageRepository.countActivePoliciesByPolicyholderId(policyholderId, "Active");
    }

    /**
     * Sums total premium for a given policyholder.
     */
    public BigDecimal calculateTotalPremiumByPolicyholder(Integer policyholderId) {
        return insuranceCoverageRepository.calculateTotalPremiumByPolicyholderId(policyholderId);
    }

    /**
     * Updates only the status of an existing coverage.
     */
    @Transactional
    public void updatePolicyStatus(Integer coverageId, PolicyStatus status) {
        insuranceCoverageRepository.findById(coverageId)
            .ifPresent(coverage -> {
                coverage.setStatus(status);
                insuranceCoverageRepository.save(coverage);
            });
    }
}
