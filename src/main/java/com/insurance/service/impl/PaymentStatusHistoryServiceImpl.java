package com.insurance.service.impl;

import com.insurance.model.PaymentStatusHistory;
import com.insurance.model.PaymentStatusHistoryId;
import com.insurance.repository.PaymentStatusHistoryRepository;
import com.insurance.service.PaymentStatusHistoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@SuppressWarnings("null")
public class PaymentStatusHistoryServiceImpl implements PaymentStatusHistoryService {

    private final PaymentStatusHistoryRepository repo;

    public PaymentStatusHistoryServiceImpl(PaymentStatusHistoryRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentStatusHistory> getStatusHistoryByPaymentId(Integer paymentId) {
        // ← هذا السطر بعد التعديل
        return repo.findByPaymentInfo_PaymentIdOrderByStatusDateDesc(paymentId);
    }

    @Override
    public PaymentStatusHistory createStatusHistory(PaymentStatusHistory statusHistory) {
        if (statusHistory == null) {
            throw new IllegalArgumentException("Payment status history cannot be null");
        }
        return repo.save(statusHistory);
    }

    @Override
    public void deleteStatusHistory(PaymentStatusHistoryId id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Payment status history not found with id: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentStatusHistory getStatusHistoryById(PaymentStatusHistoryId id) {
        return repo.findById(id)
                   .orElseThrow(() -> new EntityNotFoundException("Payment status history not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentStatusHistory> getAllStatusHistory() {
        return repo.findAll();
    }
}
