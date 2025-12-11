package com.insurance.service.impl;

import com.insurance.model.InsuranceCoverage;
import com.insurance.model.PolicyStatus;
import com.insurance.model.Policy;
import com.insurance.repository.InsuranceCoverageRepository;
import com.insurance.repository.PolicyRepository;
import com.insurance.service.PolicyService;
import com.insurance.repository.InsuranceCoverageProjection;


import org.springframework.stereotype.Service;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.JoinType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;
    private final InsuranceCoverageRepository insuranceCoverageRepository;

    public PolicyServiceImpl(PolicyRepository policyRepository,
                             InsuranceCoverageRepository insuranceCoverageRepository) {
        this.policyRepository = policyRepository;
        this.insuranceCoverageRepository = insuranceCoverageRepository;
    }

    @Override
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    @Override
    public Optional<Policy> getPolicy(Integer id) {
        return policyRepository.findById(id);
    }

    @Override
    public List<InsuranceCoverage> getPoliciesByPolicyholder(Integer policyholderId) {
        return insuranceCoverageRepository.findByPolicyholder_PolicyholderId(policyholderId);
    }

    @Override
    public InsuranceCoverage createInsuranceCoverage(InsuranceCoverage coverage) {
        return insuranceCoverageRepository.save(coverage);
    }

    @Override
    public Optional<InsuranceCoverage> updateInsuranceCoverage(Integer id, InsuranceCoverage coverage) {
        return insuranceCoverageRepository.findById(id)
            .map(existing -> {
                coverage.setInsuranceId(id);
                return insuranceCoverageRepository.save(coverage);
            });
    }

    @Override
    public Optional<InsuranceCoverage> getInsuranceCoverage(Integer id) {
        return insuranceCoverageRepository.findById(id);
    }

    @Override
    public boolean deleteInsuranceCoverage(Integer id) {
        return insuranceCoverageRepository.findById(id)
            .map(cov -> {
                insuranceCoverageRepository.delete(cov);
                return true;
            })
            .orElse(false);
    }

    /**
     * يعيد كل التغطيات المسندة لهذا الوكيل مع جلب تفاصيل الـ Policy والـ Policyholder والـ Status
     * لتجنب مشاكل Lazy Loading في الواجهة
     */
    @Override
    public List<InsuranceCoverage> findByAgentId(Integer agentId) {
        return insuranceCoverageRepository.findByAgentWithDetails(agentId);
    }

    /**
     * يعيد تغطيات هذا الوكيل بحالة معينة
     */
    @Override
    public List<InsuranceCoverage> findByAgentAndStatus(Integer agentId, PolicyStatus status) {
        return insuranceCoverageRepository.findByAgent_AgentIdAndStatus(agentId, status);
    }



    @Override
    public boolean isPolicyOwnedByPolicyholder(Integer policyId, Integer policyholderId) {
        return policyRepository.existsByPolicyIdAndPolicyholderId(policyId, policyholderId);
    }

    @Override
    public Policy getPolicyById(Integer policyId) {
        return policyRepository.findById(policyId)
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Policy not found with id: " + policyId));
    }

    /**
     * عدّ التغطيات المسندة لهذا الوكيل
     */
    @Override
    public long countByAgentId(Integer agentId) {
        return insuranceCoverageRepository.countByAgent_AgentId(agentId);
    }

    /**
     * تجديد تغطية: تحديث تاريخ الانتهاء
     */
    @Override
    public InsuranceCoverage renewInsuranceCoverage(Integer id, LocalDate newExpirationDate) {
        InsuranceCoverage cov = insuranceCoverageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Coverage not found"));
        if (!"ACTIVE".equals(cov.getStatus().getStatusName())) {
            throw new RuntimeException("Only active policies can be renewed");
        }
        cov.setExpirationDate(newExpirationDate);
        return insuranceCoverageRepository.save(cov);
    }

    /**
     * إلغاء تغطية: تغيير الحالة إلى CANCELLED
     */
    @Override
    public InsuranceCoverage cancelInsuranceCoverage(Integer id) {
        InsuranceCoverage cov = insuranceCoverageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Coverage not found"));
        PolicyStatus cancelled = new PolicyStatus();
        cancelled.setStatusName("CANCELLED");
        cov.setStatus(cancelled);
        return insuranceCoverageRepository.save(cov);
    }

    /**
     * عدّ إجمالي جميع التغطيات
     */
    @Override
    public long countAllInsuranceCoverages() {
        return insuranceCoverageRepository.countAllCoverages();
    }

    @Override
    public List<InsuranceCoverageProjection> findAllWithDetailsByAgentId(Integer agentId) {
        return insuranceCoverageRepository.findAllWithDetailsByAgentId(agentId);
    }

    @Override
    public List<InsuranceCoverageProjection> findAllWithDetailsForAdmin() {
        return insuranceCoverageRepository.findAllWithDetailsForAdmin();
    }

    // Helper method to map InsuranceCoverage entity to InsuranceCoverageProjection DTO
    private InsuranceCoverageProjection mapToProjection(InsuranceCoverage ic) {
        if (ic == null) {
            return null;
        }
        return new InsuranceCoverageProjection() {
            @Override
            public Integer getInsuranceId() {
                return ic.getInsuranceId();
            }

            @Override
            public Double getCoverageAmount() {
                // Convert BigDecimal to Double, handle null
                return ic.getCoverageAmount() != null ? ic.getCoverageAmount().doubleValue() : null;
            }

            @Override
            public Double getPremium() {
                // Convert BigDecimal to Double, handle null
                return ic.getPremium() != null ? ic.getPremium().doubleValue() : null;
            }

            @Override
            public LocalDate getExpirationDate() {
                return ic.getExpirationDate(); // Correct getter from InsuranceCoverage
            }

            @Override
            public String getPolicyType() {
                return ic.getPolicy() != null ? ic.getPolicy().getPolicyType() : null;
            }

            @Override
            public String getPolicyholderName() {
                // Concatenate first and last names, handle null policyholder or names
                if (ic.getPolicyholder() == null) {
                    return null;
                }
                String firstName = ic.getPolicyholder().getFirstName() != null ? ic.getPolicyholder().getFirstName() : "";
                String lastName = ic.getPolicyholder().getLastName() != null ? ic.getPolicyholder().getLastName() : "";
                return (firstName + " " + lastName).trim();
            }

            @Override
            public String getStatus() {
                // Use getStatusName() from PolicyStatus, handle null status
                return ic.getStatus() != null ? ic.getStatus().getStatusName() : null;
            }
        };
    }

    // Helper method to build the Specification dynamically
    private Specification<InsuranceCoverage> createSpecification(Integer agentId, Integer insuranceId, String policyType, String statusName) {
        return (Root<InsuranceCoverage> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Apply agent filter only if agentId is provided (for non-admin searches)
            if (agentId != null) {
                predicates.add(cb.equal(root.get("agent").get("agentId"), agentId));
            }

            // Add filters for optional criteria
            if (insuranceId != null) {
                predicates.add(cb.equal(root.get("insuranceId"), insuranceId));
            }
            if (policyType != null && !policyType.trim().isEmpty()) {
                // Use get instead of join for filtering to avoid multiple joins
                predicates.add(cb.equal(root.get("policy").get("policyType"), policyType));
            }
            if (statusName != null && !statusName.trim().isEmpty()) {
                // Use get instead of join for filtering to avoid multiple joins
                predicates.add(cb.equal(root.get("status").get("statusName"), statusName));
            }

           // Add fetch joins to prevent N+1 issues when accessing related entities later
           // Check if it's not a count query before adding fetches
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                 root.fetch("policy", JoinType.LEFT);
                 root.fetch("policyholder", JoinType.LEFT);
                 root.fetch("status", JoinType.LEFT);
                 root.fetch("agent", JoinType.LEFT);
                 query.distinct(true); // Use distinct because LEFT JOIN FETCH can cause duplicates
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Implementation for agent-specific criteria search
    @Override
    public List<InsuranceCoverageProjection> findPoliciesByCriteriaForAgent(Integer agentId, Integer insuranceId, String policyType, String statusName) {
        Specification<InsuranceCoverage> spec = createSpecification(agentId, insuranceId, policyType, statusName);
        List<InsuranceCoverage> results = insuranceCoverageRepository.findAll(spec);
        // Use the helper method for mapping
        return results.stream()
                .map(this::mapToProjection)
                .collect(Collectors.toList());
    }

    // Implementation for admin criteria search
    @Override
    public List<InsuranceCoverageProjection> findPoliciesByCriteriaForAdmin(Integer insuranceId, String policyType, String statusName) {
        // Pass null for agentId to ignore agent filtering for admin
        Specification<InsuranceCoverage> spec = createSpecification(null, insuranceId, policyType, statusName);
        List<InsuranceCoverage> results = insuranceCoverageRepository.findAll(spec);
        // Use the helper method for mapping
        return results.stream()
                .map(this::mapToProjection)
                .collect(Collectors.toList());
    }
}
