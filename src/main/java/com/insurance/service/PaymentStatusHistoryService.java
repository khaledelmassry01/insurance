package com.insurance.service;

import com.insurance.model.PaymentStatusHistory;
import com.insurance.model.PaymentStatusHistoryId;
import java.util.List;

public interface PaymentStatusHistoryService {

    PaymentStatusHistory createStatusHistory(PaymentStatusHistory statusHistory);

    List<PaymentStatusHistory> getStatusHistoryByPaymentId(Integer paymentId);

    PaymentStatusHistory getStatusHistoryById(PaymentStatusHistoryId id);

    void deleteStatusHistory(PaymentStatusHistoryId id);

    List<PaymentStatusHistory> getAllStatusHistory();
}
