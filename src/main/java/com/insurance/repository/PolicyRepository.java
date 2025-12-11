package com.insurance.repository;

import com.insurance.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Integer> {

    Optional<Policy> findByPolicyType(String policyType);
    
    /**
     * Check if a policy belongs to a policyholder
     * @param policyId The ID of the policy
     * @param policyholderId The ID of the policyholder
     * @return true if the policy belongs to the policyholder, false otherwise
     */
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(p) > 0 FROM Policy p JOIN p.insuranceCoverages ic JOIN ic.policyholder ph WHERE p.policyId = :policyId AND ph.policyholderId = :policyholderId")
    boolean existsByPolicyIdAndPolicyholderId(@org.springframework.data.repository.query.Param("policyId") Integer policyId, @org.springframework.data.repository.query.Param("policyholderId") Integer policyholderId);
}
