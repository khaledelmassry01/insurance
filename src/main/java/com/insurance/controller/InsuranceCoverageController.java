package com.insurance.controller;

import com.insurance.model.*;
import com.insurance.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.io.PrintWriter;
import java.io.StringWriter;

@RestController
@RequestMapping("/api/insurance-coverage")
@SuppressWarnings("null")
public class InsuranceCoverageController {

    private static final Logger log = LoggerFactory.getLogger(InsuranceCoverageController.class);

    private final PolicyRepository policyRepository;
    private final PolicyholderRepository policyholderRepository;
    private final PolicyStatusRepository policyStatusRepository;
    private final AgentRepository agentRepository;
    private final InsuranceCoverageRepository insuranceCoverageRepository;

    public InsuranceCoverageController(
            PolicyRepository policyRepository,
            PolicyholderRepository policyholderRepository,
            PolicyStatusRepository policyStatusRepository,
            AgentRepository agentRepository,
            InsuranceCoverageRepository insuranceCoverageRepository) {
        this.policyRepository = policyRepository;
        this.policyholderRepository = policyholderRepository;
        this.policyStatusRepository = policyStatusRepository;
        this.agentRepository = agentRepository;
        this.insuranceCoverageRepository = insuranceCoverageRepository;
    }

   
    @PostMapping
    public ResponseEntity<InsuranceCoverage> createCoverageFromIds(
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {

        log.info("Received request to create insurance coverage with payload: {}", payload);
        try {
            Integer policyId        = getInteger(payload, "policy_id");
            Integer policyholderId  = getInteger(payload, "policyholder_id");
            LocalDate expirationDate= getLocalDate(payload, "expiration_date");
            BigDecimal premium      = getBigDecimal(payload, "premium");
            BigDecimal coverageAmt  = getBigDecimal(payload, "coverage_amount");

            Agent agent = resolveAgent(payload, authentication);

            Policy policy = policyRepository.findById(policyId)
                    .orElseThrow(() -> badRequest("Invalid Policy ID: " + policyId));
            Policyholder policyholder = policyholderRepository.findById(policyholderId)
                    .orElseThrow(() -> badRequest("Invalid Policyholder ID: " + policyholderId));

            Integer statusId = payload.containsKey("status_id")
                    ? getInteger(payload, "status_id")
                    : null;
            PolicyStatus policyStatus = (statusId != null)
                    ? policyStatusRepository.findById(statusId).orElseGet(this::findPendingStatus)
                    : findPendingStatus();

            InsuranceCoverage coverage = new InsuranceCoverage();
            coverage.setPolicy(policy);
            coverage.setPolicyholder(policyholder);
            coverage.setStatus(policyStatus);
            coverage.setAgent(agent);
            coverage.setExpirationDate(expirationDate);
            coverage.setPremium(premium);
            coverage.setCoverageAmount(coverageAmt);

            // 5) Save and return
            InsuranceCoverage saved = insuranceCoverageRepository.save(coverage);
            log.info("Saved InsuranceCoverage with generated ID: {}", saved.getInsuranceId());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (ResponseStatusException e) {
            log.error("Bad request: {}", e.getReason());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating insurance coverage", e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            log.error("Stack trace: {}", sw.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoverage(@PathVariable Integer id) {
        if (!insuranceCoverageRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        insuranceCoverageRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsuranceCoverage> getCoverageById(@PathVariable Integer id) {
        return insuranceCoverageRepository.findByIdWithDetails(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsuranceCoverage> updateCoverage(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> payload) {

        return insuranceCoverageRepository.findByIdWithDetails(id)
            .map(existing -> {
                // Update only the editable fields
                if (payload.containsKey("expiration_date"))
                    existing.setExpirationDate(LocalDate.parse(payload.get("expiration_date").toString()));
                if (payload.containsKey("premium"))
                    existing.setPremium(new BigDecimal(payload.get("premium").toString()));
                if (payload.containsKey("coverage_amount"))
                    existing.setCoverageAmount(new BigDecimal(payload.get("coverage_amount").toString()));
                if (payload.containsKey("status_id")) {
                    Integer statusId = Integer.parseInt(payload.get("status_id").toString());
                    PolicyStatus status = policyStatusRepository.findById(statusId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status ID"));
                    existing.setStatus(status);
                }
                // Save and return
                InsuranceCoverage updated = insuranceCoverageRepository.save(existing);
                return ResponseEntity.ok(updated);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // ————— Helper methods —————

    private Agent resolveAgent(Map<String, Object> payload, Authentication auth) {
        if (payload.containsKey("agent_id")) {
            Object val = payload.get("agent_id");
            if (val instanceof Number) {
                Integer id = ((Number) val).intValue();
                return agentRepository.findById(id)
                        .orElseThrow(() -> badRequest("Agent not found with ID: " + id));
            }
            if (val instanceof String && ((String) val).contains("@")) {
                return agentRepository.findByEmail((String) val)
                        .orElseThrow(() -> badRequest("Agent not found with email: " + val));
            }
            try {
                Integer id = Integer.parseInt(val.toString());
                return agentRepository.findById(id)
                        .orElseThrow(() -> badRequest("Agent not found with ID: " + id));
            } catch (NumberFormatException ignored) { }
        }
        // fallback to authentication
        return getAgentFromAuthentication(auth);
    }

    private ResponseStatusException badRequest(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }

    private PolicyStatus findPendingStatus() {
        return policyStatusRepository.findAll().stream()
                .filter(s -> "pending".equalsIgnoreCase(s.getStatusName()))
                .findFirst()
                .orElseGet(() -> policyStatusRepository.findById(3)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Could not find 'Pending' status")));
    }

    private Agent getAgentFromAuthentication(Authentication authentication) {
        if (authentication == null) {
            return agentRepository.findById(1)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Default agent not found"));
        }
        try {
            // Try to extract a numeric agentId from details or name
            Object details = authentication.getDetails();
            if (details instanceof Map && ((Map<?,?>)details).containsKey("agentId")) {
                Integer id = ((Number)((Map<?,?>) details).get("agentId")).intValue();
                return agentRepository.findById(id)
                        .orElseThrow(() -> badRequest("Agent not found with ID: " + id));
            }
            String name = authentication.getName();
            Integer id = Integer.parseInt(name);
            return agentRepository.findById(id)
                    .orElseThrow(() -> badRequest("Agent not found with ID: " + id));
        } catch (Exception e) {
            return agentRepository.findById(1)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Default agent not found"));
        }
    }

    private Integer getInteger(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) throw new NullPointerException(key + " is missing");
        if (v instanceof Number) return ((Number)v).intValue();
        return Integer.parseInt(v.toString());
    }

    private BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) throw new NullPointerException(key + " is missing");
        if (v instanceof BigDecimal) return (BigDecimal)v;
        if (v instanceof Number) return BigDecimal.valueOf(((Number)v).doubleValue());
        return new BigDecimal(v.toString());
    }

    private LocalDate getLocalDate(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) throw new NullPointerException(key + " is missing");
        String s = v.toString();
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException e) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            return LocalDate.parse(s, fmt);
        }
    }
}
