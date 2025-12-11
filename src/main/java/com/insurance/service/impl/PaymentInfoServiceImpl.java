package com.insurance.service.impl;

import com.insurance.model.PaymentInfo;
import com.insurance.repository.PaymentInfoRepository;
import com.insurance.service.PaymentInfoService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@SuppressWarnings("null")
public class PaymentInfoServiceImpl implements PaymentInfoService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentInfoServiceImpl.class);

    private final PaymentInfoRepository paymentInfoRepository;

    public PaymentInfoServiceImpl(PaymentInfoRepository paymentInfoRepository) {
        this.paymentInfoRepository = paymentInfoRepository;
    }

    @Override
    public PaymentInfo createPayment(PaymentInfo paymentInfo) {
        if (paymentInfo == null) {
            throw new IllegalArgumentException("Payment info cannot be null");
        }
        return paymentInfoRepository.save(paymentInfo);
    }

    @Override
    public PaymentInfo updatePayment(Integer id, PaymentInfo paymentInfo) {
        if (!existsById(id)) {
            throw new EntityNotFoundException("Payment not found with id: " + id);
        }
        paymentInfo.setPaymentId(id);
        return paymentInfoRepository.save(paymentInfo);
    }

    @Override
    public void deletePayment(Integer id) {
        if (!existsById(id)) {
            throw new EntityNotFoundException("Payment not found with id: " + id);
        }
        paymentInfoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentInfo getPaymentById(Integer id) {
        return paymentInfoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentInfo> getAllPayments() {
        return paymentInfoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentInfo> getPaymentsByInsuranceId(Integer insuranceId) {
        // updated method name to match repository's underscore notation
        return paymentInfoRepository.findByInsurance_InsuranceId(insuranceId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return paymentInfoRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentInfo> getPendingPayments() {
        return paymentInfoRepository.findByPaymentDateIsNull();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentInfo> getOverduePayments(LocalDate date) {
        return paymentInfoRepository.findByDueDateBeforeAndPaymentDateIsNull(date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentInfo> getPaymentsByMethod(String paymentMethod) {
        return paymentInfoRepository.findByPaymentMethod(paymentMethod);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentInfo> getAllPaymentsByAgentId(Integer agentId) {
        return paymentInfoRepository.findByInsurance_Agent_AgentId(agentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentInfo> getPaymentsByInsuranceIdAndAgentId(Integer insuranceId, Integer agentId) {
        return paymentInfoRepository.findByInsurance_InsuranceIdAndInsurance_Agent_AgentId(insuranceId, agentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentInfo> getPaymentsByPolicyholderId(Integer policyholderId) {
        logger.info("Fetching payments for policyholderId: {}", policyholderId);
        try {
            List<PaymentInfo> payments = paymentInfoRepository.findByInsurancePolicyPolicyholderId(policyholderId);
            logger.info("Successfully fetched {} payments for policyholderId: {}", payments.size(), policyholderId);
            
            // Populate the latestStatus field for each payment and ensure policy info is loaded
            for (PaymentInfo payment : payments) {
                try {
                    // Populate the latest status
                    String status = getLatestPaymentStatus(payment.getPaymentId());
                    payment.setLatestStatus(status);
                    
                    // Populate the insuranceId field (which is the policy ID)
                    if (payment.getInsurance() != null) {
                        // Set the insurance ID (which is the policy ID)
                        payment.setInsuranceId(payment.getInsurance().getInsuranceId());
                    }
                } catch (Exception e) {
                    logger.warn("Could not process payment ID: {}. Error: {}", payment.getPaymentId(), e.getMessage());
                    // Continue with other payments even if one fails
                }
            }
            
            return payments;
        } catch (Exception e) {
            logger.error("Error fetching payments for policyholderId: {}. Error: {}", policyholderId, e.getMessage(), e);
            throw e; // Re-throw the exception to allow global error handling to catch it
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPaymentOwnedByPolicyholder(Integer paymentId, Integer policyholderId) {
        return paymentInfoRepository.existsByPaymentIdAndInsurancePolicyPolicyholderId(paymentId, policyholderId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public String getLatestPaymentStatus(Integer paymentId) {
        logger.debug("Fetching latest status for payment ID: {}", paymentId);
        try {
            String status = paymentInfoRepository.findLatestStatusByPaymentId(paymentId);
            logger.debug("Latest status for payment ID {}: {}", paymentId, status);
            return status;
        } catch (Exception e) {
            logger.error("Error fetching latest status for payment ID: {}. Error: {}", paymentId, e.getMessage(), e);
            return null; // Return null in case of error, controller will handle this appropriately
        }
    }
}
