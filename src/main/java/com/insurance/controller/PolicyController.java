package com.insurance.controller;

import com.insurance.model.Agent;
import com.insurance.model.InsuranceCoverage;
import com.insurance.model.Policy;
import com.insurance.model.PolicyStatus;
import com.insurance.repository.AgentRepository;
import com.insurance.repository.InsuranceCoverageProjection;
import com.insurance.security.AgentUserDetails;
import com.insurance.service.PolicyService;
import com.insurance.service.PolicyStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private static final Logger logger = LoggerFactory.getLogger(PolicyController.class);

    private final PolicyService policyService;
    private final PolicyStatusService policyStatusService;
    private final AgentRepository agentRepository;

    public PolicyController(PolicyService policyService, PolicyStatusService policyStatusService, AgentRepository agentRepository) {
        this.policyService = policyService;
        this.policyStatusService = policyStatusService;
        this.agentRepository = agentRepository;
    }

    // Helper to get Agent ID from Authentication principal
    private Integer getAgentIdFromAuth(Authentication auth) {
        Object principal = auth.getPrincipal();

        // If principal is AgentUserDetails, get agentId directly
        if (principal instanceof AgentUserDetails) {
            return ((AgentUserDetails) principal).getAgentId();
        }
        
        // If principal is String, it should be the agentId from JWT subject
        if (principal instanceof String) {
            try {
                return Integer.parseInt((String) principal);
            } catch (NumberFormatException e) {
                logger.error("Could not parse agentId from principal: {}", principal);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid agent ID in token");
            }
        }
        
        // If principal is UserDetails but not AgentUserDetails, try to get from username
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            Agent agent = agentRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.error("No agent found for username: {}", username);
                    return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Agent details not found for authenticated user");
                });
            return agent.getAgentId();
        }

        logger.error("Cannot determine agentId from principal of type: {}", principal.getClass().getName());
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not determine agentId from authentication principal");
    }

    /**
     * GET /api/policies
     * يعيد جميع التغطيات الخاصة بالوكيل المسجل (مع تفاصيل policy و policyholder و status)
     */
    @GetMapping
    public ResponseEntity<List<InsuranceCoverageProjection>> getMyPolicies(
            Authentication auth,
            @RequestParam(required = false) Integer insuranceId,
            @RequestParam(required = false) String policyType,
            @RequestParam(required = false) String statusName) {
        Integer agentId = null;

        // Check if user has ADMIN role
        Stream<String> rolesStream = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority);
        boolean isAdmin = rolesStream.anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!isAdmin) {
            // If not admin, get agentId using the corrected method
            agentId = getAgentIdFromAuth(auth);
        }

        // If isAdmin, agentId remains null, service method handles fetching all
        List<InsuranceCoverageProjection> list = isAdmin
            ? policyService.findPoliciesByCriteriaForAdmin(insuranceId, policyType, statusName) // Admins search all
            : policyService.findPoliciesByCriteriaForAgent(agentId, insuranceId, policyType, statusName); // Agents search own

        return ResponseEntity.ok(list);
    }

    /**
     * GET /api/policies/{id}
     * يعيد تغطية بناءً على الـ insuranceId إذا كانت تابعة للوكيل.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<InsuranceCoverage> getPolicy(
            @PathVariable Integer id,
            Authentication auth
    ) {
        Integer agentId = getAgentIdFromAuth(auth);
        return policyService.getInsuranceCoverage(id)
                .filter(cov -> cov.getAgent().getAgentId().equals(agentId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/policies
     * ينشئ تغطية جديدة ويعين الوكيل تلقائيًا من التوكن.
     */
    @PostMapping
    public ResponseEntity<InsuranceCoverage> createPolicy(
            @RequestBody InsuranceCoverage coverage,
            Authentication auth
    ) {
        coverage.getAgent().setAgentId(getAgentIdFromAuth(auth));
        InsuranceCoverage saved = policyService.createInsuranceCoverage(coverage);
        return ResponseEntity.ok(saved);
    }

    /**
     * PUT /api/policies/{id}
     * يحدث تغطية إذا كانت تابعة للوكيل.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<InsuranceCoverage> updatePolicy(
            @PathVariable Integer id,
            @RequestBody InsuranceCoverage coverage,
            Authentication auth
    ) {
        // Check if agent owns the policy before update
        Integer agentId = getAgentIdFromAuth(auth); // Use new method
        Optional<InsuranceCoverage> existingOpt = policyService.getInsuranceCoverage(id);
        if (!existingOpt.isPresent() || !existingOpt.get().getAgent().getAgentId().equals(agentId)) {
            return ResponseEntity.notFound().build(); // Or Forbidden
        }
        coverage.getAgent().setAgentId(agentId); // Ensure agent is set correctly
        // policyService.updateInsuranceCoverage returns Optional<InsuranceCoverage>
        InsuranceCoverage updated = policyService.updateInsuranceCoverage(id, coverage)
                .orElseThrow(() -> {
                    logger.error("Update failed unexpectedly for policy id {} despite prior checks.", id);
                    return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Policy update failed unexpectedly.");
                });
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/policies/{id}
     * يحذف تغطية إذا كانت تابعة للوكيل.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<Void> deletePolicy(
            @PathVariable Integer id,
            Authentication auth
    ) {
        Integer agentId = getAgentIdFromAuth(auth);
        Optional<InsuranceCoverage> coverageOpt = policyService.getInsuranceCoverage(id);

        if (coverageOpt.isPresent()) {
            InsuranceCoverage coverage = coverageOpt.get();
            if (coverage.getAgent().getAgentId().equals(agentId)) {
                policyService.deleteInsuranceCoverage(id);
                return ResponseEntity.noContent().build(); // Return 204 No Content
            } else {
                // Optional: Could return 403 Forbidden if the policy exists but belongs to another agent
                return ResponseEntity.notFound().build(); // Return 404 Not Found
            }
        } else {
            return ResponseEntity.notFound().build(); // Return 404 Not Found
        }
    }

    /**
     * PUT /api/policies/{id}/renew?newEndDate=yyyy-MM-dd
     * يجدد التغطية بتحديث تاريخ الانتهاء إذا كانت ACTIVE.
     */
    @PutMapping("/{id}/renew")
    public ResponseEntity<InsuranceCoverage> renewPolicy(
            @PathVariable Integer id,
            @RequestParam String newEndDate,
            Authentication auth
    ) {
        Integer agentId = getAgentIdFromAuth(auth);
        return policyService.getInsuranceCoverage(id)
                .filter(cov -> cov.getAgent().getAgentId().equals(agentId))
                .map(cov -> policyService.renewInsuranceCoverage(id, LocalDate.parse(newEndDate)))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/policies/{id}/cancel
     * يلغي التغطية بتغيير حالتها إلى CANCELLED.
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<InsuranceCoverage> cancelPolicy(
            @PathVariable Integer id,
            Authentication auth
    ) {
        Integer agentId = getAgentIdFromAuth(auth);
        return policyService.getInsuranceCoverage(id)
                .filter(cov -> cov.getAgent().getAgentId().equals(agentId))
                .map(cov -> policyService.cancelInsuranceCoverage(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/policies/count
     * يعيد عدد التغطيات الخاصة بالوكيل.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getPolicyCount(Authentication auth) {
        Integer agentId = getAgentIdFromAuth(auth);
        long count = policyService.countByAgentId(agentId);
        return ResponseEntity.ok(count);
    }

    /**
     * GET /api/policies/status/{status}
     * يعيد تغطيات هذا الوكيل حسب الحالة (ACTIVE, EXPIRED, ...).
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<InsuranceCoverage>> getByStatus(
            @PathVariable PolicyStatus status,
            Authentication auth
    ) {
        Integer agentId = getAgentIdFromAuth(auth);
        List<InsuranceCoverage> list = policyService.findByAgentAndStatus(agentId, status);
        return ResponseEntity.ok(list);
    }

    /**
     * GET /api/policy-statuses
     * Retrieves all available policy statuses.
     * Added for the 'Add New Policy' modal dropdown.
     */
    @GetMapping("/policy-statuses")
    public ResponseEntity<List<PolicyStatus>> getAllPolicyStatuses() {
        List<PolicyStatus> statuses = policyStatusService.getAllStatuses();
        return ResponseEntity.ok(statuses);
    }

    /**
     * GET /api/policy-types
     * Retrieves all available distinct policy types (e.g., Health, Auto) from the policies table.
     * Added for the 'Add New Policy' modal dropdown.
     */
    @GetMapping("/policy-types")
    public ResponseEntity<List<Policy>> getAllPolicyTypes() {
        List<Policy> types = policyService.getAllPolicies();
        return ResponseEntity.ok(types);
    }
}
