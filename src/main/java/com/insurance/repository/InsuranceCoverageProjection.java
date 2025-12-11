package com.insurance.repository;

import java.time.LocalDate;

public interface InsuranceCoverageProjection {
    Integer getInsuranceId();
    Double getCoverageAmount();
    Double getPremium();
    LocalDate getExpirationDate();
    String getPolicyType();
    String getPolicyholderName();
    String getStatus();
} 