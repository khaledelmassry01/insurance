package com.insurance.service;

import com.insurance.model.Policyholder;
import com.insurance.model.PaymentInfo;
import com.insurance.repository.*;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.math.BigDecimal;

import com.insurance.repository.InsuranceCoverageProjection;
import com.insurance.repository.PaymentStatusHistoryRepository;

@Service
public class ReportService {

    private final InsuranceCoverageRepository insuranceCoverageRepository;
    private final InsuranceHistoryRepository insuranceHistoryRepository;
    private final ClaimRepository claimRepository;
    private final PaymentInfoRepository paymentInfoRepository;
    private final PaymentStatusHistoryRepository paymentStatusHistoryRepository;
    private final PolicyholderRepository policyholderRepository;

    public ReportService(
            InsuranceCoverageRepository insuranceCoverageRepository,
            InsuranceHistoryRepository insuranceHistoryRepository,
            ClaimRepository claimRepository,
            PaymentInfoRepository paymentInfoRepository,
            PaymentStatusHistoryRepository paymentStatusHistoryRepository,
            PolicyholderRepository policyholderRepository
    ) {
        this.insuranceCoverageRepository = insuranceCoverageRepository;
        this.insuranceHistoryRepository = insuranceHistoryRepository;
        this.claimRepository = claimRepository;
        this.paymentInfoRepository = paymentInfoRepository;
        this.paymentStatusHistoryRepository = paymentStatusHistoryRepository;
        this.policyholderRepository = policyholderRepository;
    }

    // ******************************
    // Analytics filtered by agentId
    // ******************************

    public Map<String, Object> getPolicyholderAnalyticsByAgent(Integer agentId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> analytics = new HashMap<>();
        List<Policyholder> policyholders;
        
        if (startDate != null && endDate != null) {
            policyholders = policyholderRepository.findAllByAgentIdAndEnrollmentDateBetween(agentId, startDate, endDate);
        } else if (startDate != null) {
            policyholders = policyholderRepository.findAllByAgentIdAndEnrollmentDateBetween(agentId, startDate, LocalDate.MAX);
        } else if (endDate != null) {
            policyholders = policyholderRepository.findAllByAgentIdAndEnrollmentDateBetween(agentId, LocalDate.MIN, endDate);
        } else {
            policyholders = policyholderRepository.findAllByAgentId(agentId);
        }
        
        analytics.put("policyholders", policyholders);
        analytics.put("totalPolicyholders", policyholders.size());
        analytics.put("activePolicies", insuranceCoverageRepository.countByAgent_AgentIdAndStatus_StatusName(agentId, "Active"));
        analytics.put("totalPremium", insuranceCoverageRepository.calculateTotalPremiumByAgentId(agentId));
        return analytics;
    }

    public Map<String, Object> getFinancialReportsByAgent(Integer agentId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> financials = new HashMap<>();
        List<PaymentInfo> payments;
        
        // Filter payments by agentId and date range
        if (startDate != null && endDate != null) {
            payments = paymentInfoRepository.findByAgentIdAndPaymentDateBetween(agentId, startDate, endDate);
        } else if (startDate != null) {
            payments = paymentInfoRepository.findByAgentIdAndPaymentDateAfter(agentId, startDate);
        } else if (endDate != null) {
            payments = paymentInfoRepository.findByAgentIdAndPaymentDateBefore(agentId, endDate);
        } else {
            payments = paymentInfoRepository.findByAgentId(agentId);
        }
        
        // Set latestStatus and insuranceId for each payment
        for (PaymentInfo payment : payments) {
            var statusHistory = paymentStatusHistoryRepository.findByPaymentInfo_PaymentIdOrderByStatusDateDesc(payment.getPaymentId());
            if (!statusHistory.isEmpty()) {
                payment.setLatestStatus(statusHistory.get(0).getStatus().getStatusName());
            }
            // Set insuranceId from the insurance object
            if (payment.getInsurance() != null) {
                payment.setInsuranceId(payment.getInsurance().getInsuranceId());
            }
        }
        
        financials.put("payments", payments);
        BigDecimal totalPremium = insuranceCoverageRepository.calculateTotalPremiumByAgentId(agentId);
        long totalClaims = claimRepository.countByAgentId(agentId);

        financials.put("totalPremium", totalPremium);
        financials.put("totalClaims", totalClaims);
        return financials;
    }

    public Map<String, Object> getClaimsReportsByAgent(Integer agentId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> claims = new HashMap<>();
        long totalClaims = claimRepository.countByAgentId(agentId);
        long pendingClaims = claimRepository.countByClaimStatuses_StatusDetails_StatusName("PENDING"); // أو countByStatus if معرف

        claims.put("totalClaims", totalClaims);
        claims.put("pendingClaims", pendingClaims);
        return claims;
    }

    public long countReportsByAgent(Integer agentId) {
        // مثال: مجموع حالات:
        return insuranceCoverageRepository.countByAgent_AgentId(agentId)
             + claimRepository.countByAgentId(agentId);
    }

    // ******************************
    // الأساليب القديمة (غير مصفاة)
    // ******************************

    public Map<String, Object> getPolicyholderAnalytics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> analytics = new HashMap<>();
        List<Policyholder> policyholders;
        
        if (startDate != null && endDate != null) {
            policyholders = policyholderRepository.findAllByEnrollmentDateBetween(startDate, endDate);
        } else if (startDate != null) {
            policyholders = policyholderRepository.findAllByEnrollmentDateBetween(startDate, LocalDate.MAX);
        } else if (endDate != null) {
            policyholders = policyholderRepository.findAllByEnrollmentDateBetween(LocalDate.MIN, endDate);
        } else {
            policyholders = policyholderRepository.findAll();
        }
        
        analytics.put("policyholders", policyholders);
        analytics.put("totalPolicyholders", policyholders.size());
        analytics.put("activePolicies", insuranceCoverageRepository.countActivePoliciesByAgentId(null, "Active"));
        analytics.put("totalPremium", insuranceCoverageRepository.calculateTotalPremiumByAgentId(null));
        return analytics;
    }

    public Map<String, Object> getPolicyPerformance(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> performance = new HashMap<>();
        performance.put("activePolicies", insuranceCoverageRepository.findActivePolicies().size());
        performance.put("expiredPolicies", insuranceCoverageRepository.findExpiredPolicies().size());
        performance.put("totalPremium", insuranceCoverageRepository.calculateTotalPremiumByAgentId(null));
        return performance;
    }

    public Map<String, Object> getFinancialReports(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> financials = new HashMap<>();
        financials.put("totalPremium", insuranceCoverageRepository.calculateTotalPremiumByAgentId(null));
        financials.put("totalClaims", claimRepository.count());
        return financials;
    }

    public Map<String, Object> getClaimsReports(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("totalClaims", claimRepository.count());
        claims.put("pendingClaims", claimRepository.countByStatusName("Pending"));
        return claims;
    }

    public int getReportCount() {
        return (int) (
                insuranceCoverageRepository.count()
              + insuranceHistoryRepository.count()
              + claimRepository.count()
              + paymentInfoRepository.count()
              + paymentStatusHistoryRepository.count()
              + policyholderRepository.count()
        );
    }

    public Map<String, Object> getPolicyholderAnalyticsForAdmin(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> analytics = new HashMap<>();
        List<Policyholder> policyholders;
        
        // Use the proper repository method that includes date filtering
        if (startDate != null && endDate != null) {
            policyholders = policyholderRepository.findAllByEnrollmentDateBetween(startDate, endDate);
        } else if (startDate != null) {
            policyholders = policyholderRepository.findAllByEnrollmentDateBetween(startDate, LocalDate.MAX);
        } else if (endDate != null) {
            policyholders = policyholderRepository.findAllByEnrollmentDateBetween(LocalDate.MIN, endDate);
        } else {
            policyholders = policyholderRepository.findAll();
        }
        
        analytics.put("policyholders", policyholders);
        analytics.put("totalPolicyholders", policyholders.size());
        
        // Count active policies and calculate premium within date range
        analytics.put("activePolicies", insuranceCoverageRepository.countActivePoliciesByAgentIdAndDateRange(
            null,
            "Active",
            startDate,
            endDate
        ));
        
        // Calculate total premium for active policies within date range
        analytics.put("totalPremium", insuranceCoverageRepository.calculateTotalPremiumByDateRange(
            null,
            "Active",
            startDate,
            endDate
        ));
        
        return analytics;
    }

    public Map<String, Object> getFinancialReportsForAdmin(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> financials = new HashMap<>();
        List<PaymentInfo> payments;
        if (startDate != null && endDate != null) {
            payments = paymentInfoRepository.findAllByPaymentDateBetween(startDate, endDate);
        } else if (startDate != null) {
            payments = paymentInfoRepository.findAllByPaymentDateBetween(startDate, LocalDate.MAX);
        } else if (endDate != null) {
            payments = paymentInfoRepository.findAllByPaymentDateBetween(LocalDate.MIN, endDate);
        } else {
            payments = paymentInfoRepository.findAll();
        }
        // Set latestStatus for each payment
        for (PaymentInfo payment : payments) {
            var statusHistory = paymentStatusHistoryRepository.findByPaymentInfo_PaymentIdOrderByStatusDateDesc(payment.getPaymentId());
            if (!statusHistory.isEmpty()) {
                payment.setLatestStatus(statusHistory.get(0).getStatus().getStatusName());
            }
        }
        financials.put("payments", payments);
        BigDecimal totalPremium = insuranceCoverageRepository.calculateTotalPremium();
        long totalClaims = claimRepository.count();

        financials.put("totalPremium", totalPremium);
        financials.put("totalClaims", totalClaims);
        return financials;
    }

    public Map<String, Object> getClaimsReportsForAdmin(LocalDate startDate, LocalDate endDate) {
        return getClaimsReports(startDate, endDate); // or your actual admin logic
    }

    public List<InsuranceCoverageProjection> getPolicyPerformanceByAgent(Integer agentId, LocalDate startDate, LocalDate endDate) {
        String start = (startDate != null) ? startDate.toString() : null;
        String end = (endDate != null) ? endDate.toString() : null;
        return insuranceCoverageRepository.findAllWithDetailsByAgentIdAndExpirationDateBetween(agentId, start, end);
    }

    public List<InsuranceCoverageProjection> getPolicyPerformanceForAdmin(LocalDate startDate, LocalDate endDate) {
        String start = (startDate != null) ? startDate.toString() : null;
        String end = (endDate != null) ? endDate.toString() : null;
        return insuranceCoverageRepository.findAllWithDetailsForAdminAndExpirationDateBetween(start, end);
    }
}
