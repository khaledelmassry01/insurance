package com.insurance.repository;

import com.insurance.model.InsuranceCoverage;
import com.insurance.model.PolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InsuranceCoverageRepository extends JpaRepository<InsuranceCoverage, Integer>, JpaSpecificationExecutor<InsuranceCoverage> {

    /**
     * جميع التغطيات الخاصة بوكيل معين مع جلب تفاصيل الـ Policy والـ Policyholder والـ Status
     * لتفادي مشكلة N+1 عند التحميل
     */
    @Query("""
      SELECT ic
      FROM InsuranceCoverage ic
        JOIN FETCH ic.policy
        JOIN FETCH ic.policyholder
        JOIN FETCH ic.status
      WHERE ic.agent.agentId = :agentId
      """)
    List<InsuranceCoverage> findByAgentWithDetails(@Param("agentId") Integer agentId);

    /**
     * (احتياطية) جميع التغطيات الخاصة بوكيل معين بدون تفاصيل إضافية
     */
    List<InsuranceCoverage> findByAgent_AgentId(Integer agentId);

    /**
     * جميع التغطيات الخاصة بوكيل معين وحالة معينة
     */
    List<InsuranceCoverage> findByAgent_AgentIdAndStatus(Integer agentId, PolicyStatus status);

    /**
     * عدّ جميع التغطيات الخاصة بوكيل معين
     */
    long countByAgent_AgentId(Integer agentId);

    /**
     * جميع التغطيات الخاصة بحامل بوليصة معين
     */
    List<InsuranceCoverage> findByPolicyholder_PolicyholderId(Integer policyholderId);

    /**
     * عدّ جميع التغطيات الخاصة بحامل بوليصة معين
     */
    long countByPolicyholder_PolicyholderId(Integer policyholderId);

    /**
     * جميع التغطيات المرتبطة ببوليصة معينة
     */
    List<InsuranceCoverage> findByPolicy_PolicyId(Integer policyId);

    /**
     * جميع التغطيات ذات الحالة المحددة (نشطة، ملغاة، ... الخ)
     */
    List<InsuranceCoverage> findByStatus(PolicyStatus status);

    /**
     * عدّ التغطيات النشطة لوكيل معين عبر اسم الحالة
     */
    long countByAgent_AgentIdAndStatus_StatusName(Integer agentId, String statusName);

    /**
     * جميع التغطيات التي انتهت صلاحيتها قبل اليوم
     */
    @Query("SELECT ic FROM InsuranceCoverage ic WHERE ic.expirationDate < CURRENT_DATE")
    List<InsuranceCoverage> findExpiredPolicies();

    /**
     * جميع التغطيات التي لا تزال سارية اليوم
     */
    @Query("SELECT ic FROM InsuranceCoverage ic WHERE ic.expirationDate >= CURRENT_DATE")
    List<InsuranceCoverage> findActivePolicies();

    /**
     * مجموع أقساط وكلّاء معيّنين
     */
    @Query("SELECT COALESCE(SUM(ic.premium), 0) FROM InsuranceCoverage ic WHERE ic.agent.agentId = :agentId")
    BigDecimal calculateTotalPremiumByAgentId(@Param("agentId") Integer agentId);

    /**
     * عدّ التغطيات النشطة لحامل بوليصة معيّن عبر اسم الحالة
     */
    @Query("""
      SELECT COUNT(ic)
      FROM InsuranceCoverage ic
      WHERE ic.policyholder.policyholderId = :policyholderId
        AND ic.status.statusName = :statusName
      """)
    Long countActivePoliciesByPolicyholderId(
      @Param("policyholderId") Integer policyholderId,
      @Param("statusName") String statusName
    );

    /**
     * مجموع أقساط حامل بوليصة معيّن
     */
    @Query("SELECT COALESCE(SUM(ic.premium), 0) FROM InsuranceCoverage ic WHERE ic.policyholder.policyholderId = :policyholderId")
    BigDecimal calculateTotalPremiumByPolicyholderId(@Param("policyholderId") Integer policyholderId);

    /**
     * عدّ التغطيات النشطة لوكيل معين (بدلالة اسم الحالة)
     */
    @Query("SELECT COUNT(ic) FROM InsuranceCoverage ic WHERE ic.agent.agentId = :agentId AND ic.status.statusName = :statusName")
    long countActivePoliciesByAgentId(
      @Param("agentId") Integer agentId,
      @Param("statusName") String statusName
    );

    /**
     * جميع التغطيات ذات حالة معينة (باستخدام اسم الحالة)
     */
    @Query("SELECT ic FROM InsuranceCoverage ic WHERE ic.status.statusName = :statusName")
    List<InsuranceCoverage> findByStatusName(@Param("statusName") String statusName);

    /**
     * عدّ كل التغطيات
     */
    @Query("SELECT COUNT(ic) FROM InsuranceCoverage ic")
    long countAllCoverages();

    /**
     * مجموع أقساط كل التغطيات
     */
    @Query("SELECT COALESCE(SUM(ic.premium), 0) FROM InsuranceCoverage ic")
    BigDecimal calculateTotalPremium();

    @Query(value = "SELECT ic.insurance_id as insuranceId, ic.coverage_amount as coverageAmount, ic.premium, ic.expiration_date as expirationDate, p.policy_type as policyType, CONCAT(ph.first_name, ' ', ph.last_name) as policyholderName, ps.status_name as status FROM insurance_coverage ic JOIN policies p ON ic.policy_id = p.policy_id JOIN policyholders ph ON ic.policyholder_id = ph.policyholder_id JOIN policy_status ps ON ic.status_id = ps.status_id WHERE ic.agent_id = :agentId", nativeQuery = true)
    List<InsuranceCoverageProjection> findAllWithDetailsByAgentId(@Param("agentId") Integer agentId);

    @Query(value = "SELECT ic.insurance_id as insuranceId, ic.coverage_amount as coverageAmount, ic.premium, ic.expiration_date as expirationDate, p.policy_type as policyType, CONCAT(ph.first_name, ' ', ph.last_name) as policyholderName, ps.status_name as status FROM insurance_coverage ic JOIN policies p ON ic.policy_id = p.policy_id JOIN policyholders ph ON ic.policyholder_id = ph.policyholder_id JOIN policy_status ps ON ic.status_id = ps.status_id", nativeQuery = true)
    List<InsuranceCoverageProjection> findAllWithDetailsForAdmin();

    boolean existsByPolicyholder_PolicyholderIdAndAgent_AgentId(Integer policyholderId, Integer agentId);

    @Query("SELECT ic.insuranceId as insuranceId, p.policyType as policyType, s.statusName as statusName, ic.expirationDate as expirationDate " +
           "FROM InsuranceCoverage ic " +
           "JOIN ic.policy p " +
           "JOIN ic.status s " +
           "WHERE ic.agent.agentId = :agentId")
    List<PolicyCoverageProjection> findPolicyCoverageByAgentId(@Param("agentId") Integer agentId);

    @Query("SELECT ic.insuranceId as insuranceId, p.policyType as policyType, s.statusName as statusName, ic.expirationDate as expirationDate " +
           "FROM InsuranceCoverage ic " +
           "JOIN ic.policy p " +
           "JOIN ic.status s")
    List<PolicyCoverageProjection> findAllPolicyCoverageForAdmin();

    @Query("SELECT COALESCE(MAX(ic.insuranceId), 0) FROM InsuranceCoverage ic")
    java.util.Optional<Integer> findMaxInsuranceId();

    @Query("SELECT ic FROM InsuranceCoverage ic JOIN FETCH ic.policy JOIN FETCH ic.policyholder JOIN FETCH ic.status WHERE ic.insuranceId = :id")
    Optional<InsuranceCoverage> findByIdWithDetails(@Param("id") Integer id);

    List<InsuranceCoverage> findAllByExpirationDateBetween(LocalDate start, LocalDate end);

    @Query("""
        SELECT COUNT(ic)
        FROM InsuranceCoverage ic
        WHERE (:agentId IS NULL OR ic.agent.agentId = :agentId)
          AND UPPER(ic.status.statusName) = UPPER(:statusName)
          AND (:startDate IS NULL OR ic.policyholder.enrollmentDate >= :startDate)
          AND (:endDate IS NULL OR ic.policyholder.enrollmentDate <= :endDate)
          AND ic.policyholder.enrollmentDate IS NOT NULL
        """)
    long countActivePoliciesByAgentIdAndDateRange(
        @Param("agentId") Integer agentId,
        @Param("statusName") String statusName,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("""
        SELECT COALESCE(SUM(ic.premium), 0)
        FROM InsuranceCoverage ic
        WHERE (:agentId IS NULL OR ic.agent.agentId = :agentId)
          AND UPPER(ic.status.statusName) = UPPER(:statusName)
          AND (:startDate IS NULL OR ic.policyholder.enrollmentDate >= :startDate)
          AND (:endDate IS NULL OR ic.policyholder.enrollmentDate <= :endDate)
          AND ic.policyholder.enrollmentDate IS NOT NULL
        """)
    BigDecimal calculateTotalPremiumByDateRange(
        @Param("agentId") Integer agentId,
        @Param("statusName") String statusName,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("""
        SELECT ic
        FROM InsuranceCoverage ic
        JOIN FETCH ic.status
        JOIN FETCH ic.policy
        WHERE ic.agent.agentId = :agentId
          AND ic.expirationDate BETWEEN :startDate AND :endDate
    """)
    List<InsuranceCoverage> findAllByAgentIdAndExpirationDateBetweenWithStatusAndPolicy(
        @Param("agentId") Integer agentId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("""
        SELECT ic
        FROM InsuranceCoverage ic
        JOIN FETCH ic.status
        JOIN FETCH ic.policy
        WHERE ic.agent.agentId = :agentId
          AND (:startDate IS NULL OR ic.expirationDate >= :startDate)
          AND (:endDate IS NULL OR ic.expirationDate <= :endDate)
    """)
    List<InsuranceCoverage> findAllByAgentIdAndExpirationDateBetweenWithStatusAndPolicyNullSafe(
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

    @Query(value = "SELECT ic.insurance_id as insuranceId, ic.coverage_amount as coverageAmount, ic.premium, ic.expiration_date as expirationDate, p.policy_type as policyType, CONCAT(ph.first_name, ' ', ph.last_name) as policyholderName, ps.status_name as status FROM insurance_coverage ic JOIN policies p ON ic.policy_id = p.policy_id JOIN policyholders ph ON ic.policyholder_id = ph.policyholder_id JOIN policy_status ps ON ic.status_id = ps.status_id WHERE ic.agent_id = :agentId AND (:startDate IS NULL OR ic.expiration_date >= :startDate) AND (:endDate IS NULL OR ic.expiration_date <= :endDate)", nativeQuery = true)
    List<InsuranceCoverageProjection> findAllWithDetailsByAgentIdAndExpirationDateBetween(
        @Param("agentId") Integer agentId,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate
    );

    @Query(value = "SELECT ic.insurance_id as insuranceId, ic.coverage_amount as coverageAmount, ic.premium, ic.expiration_date as expirationDate, p.policy_type as policyType, CONCAT(ph.first_name, ' ', ph.last_name) as policyholderName, ps.status_name as status FROM insurance_coverage ic JOIN policies p ON ic.policy_id = p.policy_id JOIN policyholders ph ON ic.policyholder_id = ph.policyholder_id JOIN policy_status ps ON ic.status_id = ps.status_id WHERE (:startDate IS NULL OR ic.expiration_date >= :startDate) AND (:endDate IS NULL OR ic.expiration_date <= :endDate)", nativeQuery = true)
    List<InsuranceCoverageProjection> findAllWithDetailsForAdminAndExpirationDateBetween(
        @Param("startDate") String startDate,
        @Param("endDate") String endDate
    );
}
