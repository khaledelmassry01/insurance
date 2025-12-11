package com.insurance.service;

import com.insurance.repository.InsuranceCoverageRepository;
import com.insurance.repository.PolicyholderRepository;
import com.insurance.repository.ClaimRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    private final InsuranceCoverageRepository insuranceCoverageRepository;
    private final PolicyholderRepository policyholderRepository;
    private final ClaimRepository claimRepository;

    public DashboardService(InsuranceCoverageRepository insuranceCoverageRepository,
                          PolicyholderRepository policyholderRepository,
                          ClaimRepository claimRepository) {
        this.insuranceCoverageRepository = insuranceCoverageRepository;
        this.policyholderRepository = policyholderRepository;
        this.claimRepository = claimRepository;
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Total policyholders
        stats.put("totalPolicyholders", policyholderRepository.count());

        // Active policies
        stats.put("activePolicies", insuranceCoverageRepository.countAllCoverages());
        
        // Pending renewals - assuming these are policies expiring in the next 30 days
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysFromNow = today.plusDays(30);
        long pendingRenewals = insuranceCoverageRepository.findAllByExpirationDateBetween(today, thirtyDaysFromNow).size();
        stats.put("pendingRenewals", pendingRenewals);
        
        // Expired policies - assuming these are policies that have already expired
        long expiredPolicies = insuranceCoverageRepository.findExpiredPolicies().size();
        stats.put("expiredPolicies", expiredPolicies);

        // Total payments (premiums)
        stats.put("totalPayments", insuranceCoverageRepository.calculateTotalPremium());

        // Total claims
        stats.put("totalClaims", claimRepository.count());

        return stats;
    }
}
