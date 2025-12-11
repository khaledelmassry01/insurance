package com.insurance.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;  // ← هذا السطر مفقود لديك

@Entity
@IdClass(InsuranceHistoryId.class)
@Table(name = "insurance_history")
public class InsuranceHistory implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_id", nullable = false)
    @JsonBackReference(value = "history-insurance")
    private InsuranceCoverage insurance;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policyholder_id", nullable = false)
    @JsonBackReference(value = "history-policyholder")
    private Policyholder policyholder;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    @JsonBackReference(value = "history-status")
    private PolicyStatus status;

    @Id
    @Column(name = "status_date", nullable = false)
    private LocalDate statusDate;

    public InsuranceHistory() {}

    // ======== getters & setters ========

    public InsuranceCoverage getInsurance() {
        return insurance;
    }

    public void setInsurance(InsuranceCoverage insurance) {
        this.insurance = insurance;
    }

    public Policyholder getPolicyholder() {
        return policyholder;
    }

    public void setPolicyholder(Policyholder policyholder) {
        this.policyholder = policyholder;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public void setStatus(PolicyStatus status) {
        this.status = status;
    }

    public LocalDate getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(LocalDate statusDate) {
        this.statusDate = statusDate;
    }

    // ======== equals & hashCode ========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InsuranceHistory)) return false;
        InsuranceHistory that = (InsuranceHistory) o;
        return Objects.equals(insurance, that.insurance) &&
               Objects.equals(policyholder, that.policyholder) &&
               Objects.equals(status, that.status) &&
               Objects.equals(statusDate, that.statusDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(insurance, policyholder, status, statusDate);
    }
}
