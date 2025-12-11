package com.insurance.controller;

import com.insurance.model.InsuranceCoverage;
import com.insurance.model.PaymentInfo;
import com.insurance.model.PaymentStatusHistory;
import com.insurance.model.Policyholder;
import com.insurance.service.InsuranceCoverageService;
import com.insurance.service.PaymentInfoService;
import com.insurance.service.PaymentStatusHistoryService;
import com.insurance.service.PolicyholderService;
import com.insurance.service.PolicyService;
import com.insurance.repository.PaymentStatusRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling policyholder-specific payment operations
 */
@RestController
@RequestMapping("/api/policyholder/payments")
public class PolicyholderPaymentController {
    
    private static final Logger logger = LoggerFactory.getLogger(PolicyholderPaymentController.class);
    
    private final PaymentInfoService paymentInfoService;
    private final PolicyService policyService;
    private final PolicyholderService policyholderService;
    private final PaymentStatusRepository paymentStatusRepository;
    private final PaymentStatusHistoryService statusHistoryService;
    private final InsuranceCoverageService insuranceCoverageService;
    
    public PolicyholderPaymentController(
            PaymentInfoService paymentInfoService,
            PolicyService policyService,
            PolicyholderService policyholderService,
            PaymentStatusRepository paymentStatusRepository,
            PaymentStatusHistoryService statusHistoryService,
            InsuranceCoverageService insuranceCoverageService) {
        this.paymentInfoService = paymentInfoService;
        this.policyService = policyService;
        this.policyholderService = policyholderService;
        this.paymentStatusRepository = paymentStatusRepository;
        this.statusHistoryService = statusHistoryService;
        this.insuranceCoverageService = insuranceCoverageService;
    }
    
    /**
     * Get all payments for the authenticated policyholder
     */
    @GetMapping
    public ResponseEntity<List<PaymentInfo>> getPolicyholderPayments(Authentication auth) {
        logger.info("Attempting to get policyholder payments. Authentication: {}", auth.getName());
        // Get policyholder ID from the authentication token
        String email = auth.getName();
        Policyholder policyholder = policyholderService.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid policyholder"));
        
        // Get all payments for this policyholder's policies
        List<PaymentInfo> payments = paymentInfoService.getPaymentsByPolicyholderId(policyholder.getPolicyholderId());
        return ResponseEntity.ok(payments);
    }
    
    /**
     * Get a specific payment receipt by ID for the authenticated policyholder
     */
    @GetMapping("/{id}/receipt")
    public ResponseEntity<Map<String, Object>> getPaymentReceipt(@PathVariable Integer id, Authentication auth) {
        // Get policyholder ID from the authentication token
        String email = auth.getName();
        Policyholder policyholder = policyholderService.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid policyholder"));
        
        // Get the payment and verify it belongs to this policyholder
        PaymentInfo payment = paymentInfoService.getPaymentById(id);
        
        // Verify the payment belongs to one of the policyholder's policies
        if (!paymentInfoService.isPaymentOwnedByPolicyholder(payment.getPaymentId(), policyholder.getPolicyholderId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Payment does not belong to this policyholder");
        }
        
        // Get the latest payment status from the database
        String latestStatus = paymentInfoService.getLatestPaymentStatus(payment.getPaymentId());
        logger.debug("Latest status for payment ID {}: {}", payment.getPaymentId(), latestStatus);
        
        // Only generate receipts for paid payments
        if (latestStatus == null || !"Paid".equals(latestStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Receipt only available for paid payments");
        }
        
        // Create receipt data
        Map<String, Object> receipt = new HashMap<>();
        receipt.put("paymentId", payment.getPaymentId());
        receipt.put("policyId", payment.getInsurance().getInsuranceId());
        receipt.put("amount", payment.getAmount());
        receipt.put("paymentDate", payment.getPaymentDate());
        receipt.put("paymentMethod", payment.getPaymentMethod());
        receipt.put("status", "Paid");
        receipt.put("generatedDate", LocalDate.now());
        
        return ResponseEntity.ok(receipt);
    }
    
    /**
     * Make a payment for the authenticated policyholder
     */
    @PostMapping
    public ResponseEntity<PaymentInfo> makePayment(@RequestBody Map<String, Object> paymentData, Authentication auth) {
        // Get policyholder ID from the authentication token
        String email = auth.getName();
        Policyholder policyholder = policyholderService.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid policyholder"));
        
        // Extract payment data
        Integer policyId = Integer.valueOf(paymentData.get("policyId").toString());
        Double amount = Double.valueOf(paymentData.get("amount").toString());
        String paymentMethod = (String) paymentData.get("paymentMethod");
        
        // Verify the policy belongs to this policyholder
        if (!policyService.isPolicyOwnedByPolicyholder(policyId, policyholder.getPolicyholderId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Policy does not belong to this policyholder");
        }
        
        // Create and save the payment
        PaymentInfo payment = new PaymentInfo();
        // Get insurance coverage for the policy
        List<InsuranceCoverage> coverages = insuranceCoverageService.getInsuranceCoveragesByPolicyId(policyId);
        if (coverages.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No insurance coverage found for this policy");
        }
        payment.setInsurance(coverages.get(0)); // Use the first coverage
        payment.setAmount(new BigDecimal(amount));
        payment.setInstallmentNumber(1); // Default to first installment
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod(paymentMethod);
        payment.setLatestStatus("Pending");
        payment.setDueDate(LocalDate.now().plusMonths(1)); // Default due date to one month from now
        
        // Save the payment
        PaymentInfo savedPayment = paymentInfoService.createPayment(payment);
        
        // Add an initial status history entry (Paid)
        PaymentStatusHistory history = new PaymentStatusHistory();
        history.setPaymentInfo(savedPayment);
        history.setStatus(
            paymentStatusRepository.findById(2) // Assuming 2 is the ID for "Paid" status
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Paid status not found"))
        );
        history.setStatusDate(LocalDate.now());
        statusHistoryService.createStatusHistory(history);
        
        // Populate transient fields for the response
        savedPayment.setLatestStatus("Paid"); // Initial status
        if (savedPayment.getInsurance() != null) {
            savedPayment.setInsuranceId(savedPayment.getInsurance().getInsuranceId()); // This is the policy ID
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPayment);
    }
}
