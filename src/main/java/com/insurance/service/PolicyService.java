package com.insurance.service;

import com.insurance.model.InsuranceCoverage;
import com.insurance.model.Policy;
import com.insurance.model.PolicyStatus;
import com.insurance.repository.InsuranceCoverageProjection;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PolicyService {
    /**
     * استرجاع جميع أنواع البوليصات (Policy) المسجلة في النظام
     */
    List<Policy> getAllPolicies();

    /**
     * استرجاع بوليصة واحدة بحسب الـ ID الخاص بها
     */
    Optional<Policy> getPolicy(Integer id);

    /**
     * استرجاع جميع تغطيات التأمين (InsuranceCoverage) الخاصة بحامل بوليصة معين
     */
    List<InsuranceCoverage> getPoliciesByPolicyholder(Integer policyholderId);

    /**
     * إنشاء تغطية تأمين جديدة
     */
    InsuranceCoverage createInsuranceCoverage(InsuranceCoverage coverage);

    /**
     * تحديث تغطية تأمين موجودة
     */
    Optional<InsuranceCoverage> updateInsuranceCoverage(Integer id, InsuranceCoverage coverage);

    /**
     * حذف تغطية تأمين بحسب الـ ID
     */
    boolean deleteInsuranceCoverage(Integer id);

    /**
     * استرجاع تغطية تأمين واحدة بحسب الـ ID
     */
    Optional<InsuranceCoverage> getInsuranceCoverage(Integer id);

    /**
     * استرجاع جميع تغطيات التأمين المسندة لوكيل معين
     */
    List<InsuranceCoverage> findByAgentId(Integer agentId);
    
    /**
     * Check if a policy belongs to a policyholder
     * @param policyId The ID of the policy
     * @param policyholderId The ID of the policyholder
     * @return true if the policy belongs to the policyholder, false otherwise
     */
    boolean isPolicyOwnedByPolicyholder(Integer policyId, Integer policyholderId);
    
    /**
     * Get policy by ID
     * @param policyId The ID of the policy
     * @return The policy
     */
    Policy getPolicyById(Integer policyId);

    /**
     * استرجاع تغطيات تأمين لوكيل معين بحسب الحالة (ACTIVE، CANCELLED، ...)
     */
    List<InsuranceCoverage> findByAgentAndStatus(Integer agentId, PolicyStatus status);

    /**
     * عدّ عدد تغطيات التأمين المسندة لوكيل معين
     */
    long countByAgentId(Integer agentId);

    /**
     * تجديد تغطية تأمين (تغيير تاريخ الانتهاء)
     */
    InsuranceCoverage renewInsuranceCoverage(Integer id, LocalDate newExpirationDate);

    /**
     * إلغاء تغطية تأمين (تغيير الحالة إلى CANCELLED)
     */
    InsuranceCoverage cancelInsuranceCoverage(Integer id);

    /**
     * عدّ إجمالي عدد جميع تغطيات التأمين في النظام
     */
    long countAllInsuranceCoverages();

    List<InsuranceCoverageProjection> findAllWithDetailsByAgentId(Integer agentId);

    List<InsuranceCoverageProjection> findAllWithDetailsForAdmin();

    // Added method for agent-specific criteria search
    List<InsuranceCoverageProjection> findPoliciesByCriteriaForAgent(Integer agentId, Integer insuranceId, String policyType, String statusName);

    // Added method for admin criteria search
    List<InsuranceCoverageProjection> findPoliciesByCriteriaForAdmin(Integer insuranceId, String policyType, String statusName);
}
