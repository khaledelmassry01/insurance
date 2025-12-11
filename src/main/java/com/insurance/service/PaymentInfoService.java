package com.insurance.service;

import com.insurance.model.PaymentInfo;
import java.time.LocalDate;
import java.util.List;

public interface PaymentInfoService {
    PaymentInfo createPayment(PaymentInfo paymentInfo);
    PaymentInfo updatePayment(Integer id, PaymentInfo paymentInfo);
    void deletePayment(Integer id);
    PaymentInfo getPaymentById(Integer id);
    List<PaymentInfo> getAllPayments();
    List<PaymentInfo> getPaymentsByInsuranceId(Integer insuranceId);
    boolean existsById(Integer id);
    List<PaymentInfo> getPendingPayments();
    List<PaymentInfo> getOverduePayments(LocalDate date);
    List<PaymentInfo> getPaymentsByMethod(String paymentMethod);
    List<PaymentInfo> getAllPaymentsByAgentId(Integer agentId);
    List<PaymentInfo> getPaymentsByInsuranceIdAndAgentId(Integer insuranceId, Integer agentId);
    
    /**
     * Get all payments for a policyholder
     * @param policyholderId The ID of the policyholder
     * @return List of payments for the policyholder's policies
     */
    List<PaymentInfo> getPaymentsByPolicyholderId(Integer policyholderId);
    
    /**
     * Check if a payment belongs to a policyholder
     * @param paymentId The ID of the payment
     * @param policyholderId The ID of the policyholder
     * @return true if the payment belongs to the policyholder, false otherwise
     */
    boolean isPaymentOwnedByPolicyholder(Integer paymentId, Integer policyholderId);
    
    /**
     * Get the latest status for a payment
     * @param paymentId The ID of the payment
     * @return The latest status name (e.g., "Paid", "Pending", etc.) or null if no status exists
     */
    String getLatestPaymentStatus(Integer paymentId);
}