package com.insurance.repository;

import com.insurance.model.Policyholder;
import com.insurance.model.InsuranceCoverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyholderRepository extends JpaRepository<Policyholder, Integer> {
    Optional<Policyholder> findByEmail(String email);
    Optional<Policyholder> findBySsn(String ssn);
    boolean existsByEmail(String email);
    
    @Query("SELECT ic FROM InsuranceCoverage ic WHERE ic.policyholder.policyholderId = :policyholderId")
    List<InsuranceCoverage> findPoliciesByPolicyholderId(@Param("policyholderId") Integer policyholderId);
    
    @Query("SELECT COUNT(ic) FROM InsuranceCoverage ic WHERE ic.policyholder.policyholderId = :policyholderId AND ic.status.statusName = 'ACTIVE'")
    Long countActivePoliciesByPolicyholderId(@Param("policyholderId") Integer policyholderId);
    
    @Query("SELECT COALESCE(SUM(ic.premium), 0) FROM InsuranceCoverage ic WHERE ic.policyholder.policyholderId = :policyholderId")
    BigDecimal calculateTotalPremiumByPolicyholderId(@Param("policyholderId") Integer policyholderId);

    public interface PolicyholderWithStatusProjection {
        Integer getPolicyholderId();
        String getFirstName();
        String getLastName();
        String getEmail();
        String getPhoneNumber();
        String getStatus();
    }

    @Query(value = "WITH LatestCoverageByAgent AS ( " +
            "    SELECT " +
            "        ic.policyholder_id, " +
            "        ic.status_id, " +
            "        ROW_NUMBER() OVER(PARTITION BY ic.policyholder_id ORDER BY ic.expiration_date DESC, ic.insurance_id DESC) as rn " +
            "    FROM insurance_coverage ic " +
            "    WHERE ic.agent_id = :agentId " +
            ") " +
            "SELECT " +
            "    ph.policyholder_id as policyholderId, " +
            "    ph.first_name as firstName, " +
            "    ph.last_name as lastName, " +
            "    ph.email, " +
            "    ph.phone_number as phoneNumber, " +
            "    ps.status_name as status " +
            "FROM policyholders ph " +
            "INNER JOIN LatestCoverageByAgent lc ON ph.policyholder_id = lc.policyholder_id AND lc.rn = 1 " +
            "LEFT JOIN policy_status ps ON lc.status_id = ps.status_id",
            nativeQuery = true)
    List<PolicyholderWithStatusProjection> findAllWithLatestStatusByAgentId(@Param("agentId") Integer agentId);

    @Query(value = "WITH LatestCoverage AS ( " +
            "    SELECT " +
            "        ic.policyholder_id, " +
            "        ic.status_id, " +
            // Rank coverages per policyholder, latest expiration date first. Tie-break with ID.
            "        ROW_NUMBER() OVER(PARTITION BY ic.policyholder_id ORDER BY ic.expiration_date DESC, ic.insurance_id DESC) as rn " +
            "    FROM insurance_coverage ic " +
            ") " +
            "SELECT " +
            "    ph.policyholder_id as policyholderId, " +
            "    ph.first_name as firstName, " +
            "    ph.last_name as lastName, " +
            "    ph.email, " +
            "    ph.phone_number as phoneNumber, " +
            "    ps.status_name as status " +
            "FROM policyholders ph " +
            // Left join to include all policyholders
            "LEFT JOIN LatestCoverage lc ON ph.policyholder_id = lc.policyholder_id AND lc.rn = 1 " +
            // Left join to get status only if a latest coverage exists and has a status
            "LEFT JOIN policy_status ps ON lc.status_id = ps.status_id",
            nativeQuery = true)
    List<PolicyholderWithStatusProjection> findAllWithLatestStatus();

    @Query("SELECT ph FROM Policyholder ph JOIN InsuranceCoverage ic ON ph.policyholderId = ic.policyholder.policyholderId WHERE ic.agent.agentId = :agentId")
    List<Policyholder> findAllByAgentId(@Param("agentId") Integer agentId);

    @Query("""
        SELECT DISTINCT p FROM Policyholder p 
        WHERE p.enrollmentDate >= :startDate 
        AND p.enrollmentDate <= :endDate
        AND p.enrollmentDate IS NOT NULL
        ORDER BY p.enrollmentDate DESC
        """)
    List<Policyholder> findAllByEnrollmentDateBetween(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("""
        SELECT DISTINCT p FROM Policyholder p 
        JOIN InsuranceCoverage ic ON p.policyholderId = ic.policyholder.policyholderId 
        WHERE ic.agent.agentId = :agentId 
        AND p.enrollmentDate >= :startDate 
        AND p.enrollmentDate <= :endDate
        AND p.enrollmentDate IS NOT NULL
        ORDER BY p.enrollmentDate DESC
        """)
    List<Policyholder> findAllByAgentIdAndEnrollmentDateBetween(
        @Param("agentId") Integer agentId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("""
        SELECT ic
        FROM InsuranceCoverage ic
        JOIN FETCH ic.status
        JOIN FETCH ic.policy
        WHERE (:startDate IS NULL OR ic.expirationDate >= :startDate)
          AND (:endDate IS NULL OR ic.expirationDate <= :endDate)
    """)
    List<InsuranceCoverage> findAllWithStatusAndPolicyNullSafe(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
} 