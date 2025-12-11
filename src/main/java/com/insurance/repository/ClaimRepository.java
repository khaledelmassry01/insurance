package com.insurance.repository;

import com.insurance.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Integer> {

    @Query("SELECT c FROM Claim c WHERE c.insuranceCoverage.insuranceId = :insuranceId")
    List<Claim> findByInsuranceId(@Param("insuranceId") Integer insuranceId);

    @Query("SELECT c FROM Claim c WHERE c.policyholder.policyholderId = :policyholderId")
    List<Claim> findByPolicyholderId(@Param("policyholderId") Integer policyholderId);

    @Query("SELECT c FROM Claim c WHERE c.agent.agentId = :agentId")
    List<Claim> findByAgentId(@Param("agentId") Integer agentId);

    @Query("SELECT c FROM Claim c JOIN c.claimStatuses cs JOIN cs.statusDetails s "
         + "WHERE s.statusName = :statusName")
    List<Claim> findByStatusName(@Param("statusName") String statusName);

    @Query("SELECT c FROM Claim c WHERE c.insuranceCoverage.policy.policyId = :policyId")
    List<Claim> findByPolicyId(@Param("policyId") Integer policyId);

    Optional<Claim> findByClaimId(Integer claimId);

    @Query("SELECT COUNT(c) FROM Claim c WHERE c.insuranceCoverage.insuranceId = :insuranceId")
    Long countByInsuranceId(@Param("insuranceId") Integer insuranceId);

    @Query("SELECT COUNT(c) FROM Claim c WHERE c.policyholder.policyholderId = :policyholderId")
    Long countByPolicyholderId(@Param("policyholderId") Integer policyholderId);

    @Query("SELECT COUNT(c) FROM Claim c WHERE c.agent.agentId = :agentId")
    Long countByAgentId(@Param("agentId") Integer agentId);

    /** صريح: عدّ Claims حسب اسم الحالة عبر JOIN */
    @Query("""
           SELECT COUNT(c)
             FROM Claim c
             JOIN c.claimStatuses cs
             JOIN cs.statusDetails s
            WHERE s.statusName = :statusName
           """)
    long countByStatusName(@Param("statusName") String statusName);
    
    /**
     * Find claims by policyholder ID
     */
    @Query("SELECT c FROM Claim c WHERE c.policyholder.policyholderId = :policyholderId")
    List<Claim> findByPolicyPolicyholderId(@Param("policyholderId") Integer policyholderId);
    
    /**
     * Check if a claim belongs to a policyholder
     */
    @Query("SELECT COUNT(c) > 0 FROM Claim c WHERE c.claimId = :claimId AND c.policyholder.policyholderId = :policyholderId")
    boolean existsByClaimIdAndPolicyPolicyholderId(@Param("claimId") Integer claimId, @Param("policyholderId") Integer policyholderId);

    /** مثال للتنقل عبر المسار (يناسب حالة واحد إلى واحد أو عنصر واحد في مجموعة) */
    List<Claim> findByClaimStatuses_StatusDetails_StatusId(Integer statusId);

    Long countByClaimStatuses_StatusDetails_StatusId(Integer statusId);

    @Query("SELECT COUNT(c) FROM Claim c JOIN c.claimStatuses cs JOIN cs.statusDetails s WHERE s.statusName = :statusName")
    long countByClaimStatuses_StatusDetails_StatusName(@Param("statusName") String statusName);

    // Projection interface (if you want to avoid exposing the full Claim entity)
    public interface ClaimProjection {
        Integer getClaimId();
        // add other fields you want to expose
    }

    // For AGENT
    @Query("SELECT c FROM Claim c WHERE c.agent.agentId = :agentId")
    List<Claim> findAllByAgentId(@Param("agentId") Integer agentId);

    // For ADMIN
    @Query("SELECT c FROM Claim c")
    List<Claim> findAllForAdmin();

}
