package com.insurance.repository;

import java.time.LocalDate;

public interface PolicyCoverageProjection {
    Integer getInsuranceId();
    String getPolicyType();
    String getStatusName();
    LocalDate getExpirationDate();
}
