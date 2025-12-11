package com.insurance.controller;

import com.insurance.model.PaymentInfo;
import com.insurance.model.PaymentStatusHistory;
import com.insurance.model.Agent;
import com.insurance.repository.AgentRepository;
import com.insurance.security.AgentUserDetails;
import com.insurance.service.PaymentInfoService;
import com.insurance.service.PaymentStatusHistoryService;
import com.insurance.repository.PaymentStatusRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@SuppressWarnings("null")
public class PaymentInfoController {

    private final PaymentInfoService paymentInfoService;
    private final PaymentStatusRepository paymentStatusRepository;
    private final PaymentStatusHistoryService statusHistoryService;
    private final AgentRepository agentRepository;

    public PaymentInfoController(
            PaymentInfoService paymentInfoService,
            PaymentStatusRepository paymentStatusRepository,
            PaymentStatusHistoryService statusHistoryService,
            AgentRepository agentRepository) {
        this.paymentInfoService = paymentInfoService;
        this.paymentStatusRepository = paymentStatusRepository;
        this.statusHistoryService = statusHistoryService;
        this.agentRepository = agentRepository;
    }
    
    private Integer getAgentIdFromAuth(Authentication auth) {
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

    @PostMapping
    public ResponseEntity<PaymentInfo> createPayment(@Valid @RequestBody PaymentInfo paymentInfo) {
        // save the payment
        PaymentInfo saved = paymentInfoService.createPayment(paymentInfo);

        // add an initial status history entry
        PaymentStatusHistory h = new PaymentStatusHistory();
        h.setPaymentInfo(saved);
        h.setStatus(
            paymentStatusRepository.findById(1)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Initial status not found"))
        );
        h.setStatusDate(LocalDate.now());
        statusHistoryService.createStatusHistory(h);

        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<PaymentInfo>> getAllPayments(Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMIN"));
        Integer agentId = getAgentIdFromAuth(auth);
        List<PaymentInfo> payments = isAdmin
            ? paymentInfoService.getAllPayments()
            : paymentInfoService.getAllPaymentsByAgentId(agentId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<PaymentInfo> getPayment(@PathVariable Integer id) {
        PaymentInfo payment = paymentInfoService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/insurance/{insuranceId}")
    public ResponseEntity<List<PaymentInfo>> getPaymentsByInsurance(@PathVariable Integer insuranceId, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMIN"));
        Integer agentId = getAgentIdFromAuth(auth);
        List<PaymentInfo> payments = isAdmin
            ? paymentInfoService.getPaymentsByInsuranceId(insuranceId)
            : paymentInfoService.getPaymentsByInsuranceIdAndAgentId(insuranceId, agentId);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/{paymentId}/status")
    public ResponseEntity<PaymentStatusHistory> updatePaymentStatus(
            @PathVariable Integer paymentId,
            @RequestParam Integer statusId) {

        var payment = paymentInfoService.getPaymentById(paymentId);
        var status  = paymentStatusRepository.findById(statusId)
                          .orElseThrow(() -> new ResponseStatusException(
                              HttpStatus.BAD_REQUEST, "Invalid status ID"));

        PaymentStatusHistory h = new PaymentStatusHistory();
        h.setPaymentInfo(payment);
        h.setStatus(status);
        h.setStatusDate(LocalDate.now());

        return ResponseEntity.ok(statusHistoryService.createStatusHistory(h));
    }

    @GetMapping("/{paymentId}/status-history")
    public ResponseEntity<List<PaymentStatusHistory>> getStatusHistory(@PathVariable Integer paymentId) {
        // will throw 404 if not found
        paymentInfoService.getPaymentById(paymentId);

        var history = statusHistoryService.getStatusHistoryByPaymentId(paymentId);
        return ResponseEntity.ok(history);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<PaymentInfo> updatePayment(@PathVariable Integer id, @RequestBody PaymentInfo paymentInfo) {
        PaymentInfo updated = paymentInfoService.updatePayment(id, paymentInfo);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<Void> deletePayment(@PathVariable Integer id) {
        paymentInfoService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
