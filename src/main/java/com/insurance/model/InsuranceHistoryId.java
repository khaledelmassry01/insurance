package com.insurance.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Composite key for InsuranceHistory.
 */
@Embeddable
public class InsuranceHistoryId implements Serializable {

    private static final long serialVersionUID = 1L;

    // يجب أن تتطابق أسماء الحقول هنا مع أسماء خصائص الـ entity (InsuranceHistory)
    private Integer insurance;
    private Integer policyholder;
    private Integer status;
    private LocalDate statusDate;

    public InsuranceHistoryId() {}

    public InsuranceHistoryId(Integer insurance,
                              Integer policyholder,
                              Integer status,
                              LocalDate statusDate) {
        this.insurance = insurance;
        this.policyholder = policyholder;
        this.status = status;
        this.statusDate = statusDate;
    }

    public Integer getInsurance() {
        return insurance;
    }

    public void setInsurance(Integer insurance) {
        this.insurance = insurance;
    }

    public Integer getPolicyholder() {
        return policyholder;
    }

    public void setPolicyholder(Integer policyholder) {
        this.policyholder = policyholder;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDate getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(LocalDate statusDate) {
        this.statusDate = statusDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InsuranceHistoryId that = (InsuranceHistoryId) o;
        return Objects.equals(insurance, that.insurance)
            && Objects.equals(policyholder, that.policyholder)
            && Objects.equals(status, that.status)
            && Objects.equals(statusDate, that.statusDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(insurance, policyholder, status, statusDate);
    }
}
