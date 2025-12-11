package com.insurance.service;

import com.insurance.model.PaymentStatus;
import java.util.List;

public interface PaymentStatusService {
    PaymentStatus createStatus(PaymentStatus status);
    PaymentStatus updateStatus(Integer id, PaymentStatus status);
    void deleteStatus(Integer id);
    PaymentStatus getStatusById(Integer id);
    List<PaymentStatus> getAllStatuses();
    boolean existsById(Integer id);
} 