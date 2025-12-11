package com.insurance.controller;

import com.insurance.model.Agent;
import com.insurance.repository.AgentRepository;
import com.insurance.security.AgentUserDetails;
import com.insurance.service.ReportService;
import com.insurance.repository.InsuranceCoverageProjection;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
// تأمين جميع المسارات لوكلاء فقط
public class ReportController {

    private final ReportService reportService;
    private final AgentRepository agentRepository;

    public ReportController(ReportService reportService, AgentRepository agentRepository) {
        this.reportService = reportService;
        this.agentRepository = agentRepository;
    }

    /**
     * يستخرج agentId من تفاصيل المستخدم المصادق عليه (JWT)
     */
    private Integer currentAgentId(Authentication auth) {
        Object principal = auth.getPrincipal();
        
        if (principal instanceof AgentUserDetails) {
            return ((AgentUserDetails) principal).getAgentId();
        }
        
        if (principal instanceof String) {
            return Integer.parseInt((String) principal);
        }
        
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            Optional<Agent> agentOpt = agentRepository.findByEmail(email);
            return agentOpt.map(Agent::getAgentId).orElse(null);
        }
        
        return null;
    }

    @GetMapping("/policyholder-analytics")
    public ResponseEntity<Map<String, Object>> getPolicyholderAnalytics(
            Authentication auth,
            @RequestParam(name = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "end", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        boolean isAdmin = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMIN"));
        Integer agentId = currentAgentId(auth);

        Map<String, Object> data = isAdmin
            ? reportService.getPolicyholderAnalyticsForAdmin(startDate, endDate)
            : reportService.getPolicyholderAnalyticsByAgent(agentId, startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/policy-performance")
    public ResponseEntity<List<InsuranceCoverageProjection>> getPolicyPerformance(
            Authentication auth,
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        boolean isAdmin = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMIN"));
        Integer agentId = null;
        try {
            agentId = currentAgentId(auth);
        } catch (Exception e) {
            System.out.println("Could not extract agentId: " + e.getMessage());
        }
        System.out.println("/policy-performance called. agentId = " + agentId + ", isAdmin = " + isAdmin);
        try {
            List<InsuranceCoverageProjection> data = isAdmin
                ? reportService.getPolicyPerformanceForAdmin(startDate, endDate)
                : reportService.getPolicyPerformanceByAgent(agentId, startDate, endDate);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }

    @GetMapping("/financial")
    public ResponseEntity<Map<String, Object>> getFinancialReports(
            Authentication auth,
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        boolean isAdmin = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMIN"));
        Integer agentId = currentAgentId(auth);

        Map<String, Object> data = isAdmin
            ? reportService.getFinancialReportsForAdmin(startDate, endDate)
            : reportService.getFinancialReportsByAgent(agentId, startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/claims")
    public ResponseEntity<Map<String, Object>> getClaimsReports(
            Authentication auth,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        boolean isAdmin = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMIN"));
        Integer agentId = currentAgentId(auth);

        Map<String, Object> data = isAdmin
            ? reportService.getClaimsReportsForAdmin(startDate, endDate)
            : reportService.getClaimsReportsByAgent(agentId, startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getReportCount(Authentication auth) {
        Integer agentId = currentAgentId(auth);
        long count = reportService.countReportsByAgent(agentId);
        return ResponseEntity.ok(count);
    }
}
