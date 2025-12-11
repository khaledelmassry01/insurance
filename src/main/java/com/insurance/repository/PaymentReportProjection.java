package com.insurance.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PaymentReportProjection {
    Integer getPaymentId();
    BigDecimal getAmount();
    LocalDate getPaymentDate();
    String getStatus();
} 