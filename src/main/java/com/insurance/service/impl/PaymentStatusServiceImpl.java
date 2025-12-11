package com.insurance.service.impl;

import com.insurance.model.PaymentStatus;
import com.insurance.repository.PaymentStatusRepository;
import com.insurance.service.PaymentStatusService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@SuppressWarnings("null")
public class PaymentStatusServiceImpl implements PaymentStatusService {

    private final PaymentStatusRepository paymentStatusRepository;

    public PaymentStatusServiceImpl(PaymentStatusRepository paymentStatusRepository) {
        this.paymentStatusRepository = paymentStatusRepository;
    }

    @Override
    public PaymentStatus createStatus(PaymentStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Payment status cannot be null");
        }
        return paymentStatusRepository.save(status);
    }

    @Override
    public PaymentStatus updateStatus(Integer id, PaymentStatus status) {
        if (!existsById(id)) {
            throw new EntityNotFoundException("Payment status not found with id: " + id);
        }
        status.setStatusId(id);
        return paymentStatusRepository.save(status);
    }

    @Override
    public void deleteStatus(Integer id) {
        if (!existsById(id)) {
            throw new EntityNotFoundException("Payment status not found with id: " + id);
        }
        paymentStatusRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentStatus getStatusById(Integer id) {
        return paymentStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment status not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentStatus> getAllStatuses() {
        return paymentStatusRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return paymentStatusRepository.existsById(id);
    }
} 