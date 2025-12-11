// ClaimStatusId.java
package com.insurance.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class ClaimStatusId implements Serializable {
    private Integer claim;
    private Integer statusDetails;
    private LocalDate statusDate;

    public ClaimStatusId() {}

    public ClaimStatusId(Integer claim, Integer statusDetails, LocalDate statusDate) {
        this.claim = claim;
        this.statusDetails = statusDetails;
        this.statusDate = statusDate;
    }

    public Integer getClaim() {
        return claim;
    }

    public void setClaim(Integer claim) {
        this.claim = claim;
    }

    public Integer getStatusDetails() {
        return statusDetails;
    }

    public void setStatusDetails(Integer statusDetails) {
        this.statusDetails = statusDetails;
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
        if (!(o instanceof ClaimStatusId)) return false;
        ClaimStatusId that = (ClaimStatusId) o;
        return Objects.equals(claim, that.claim)
            && Objects.equals(statusDetails, that.statusDetails)
            && Objects.equals(statusDate, that.statusDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(claim, statusDetails, statusDate);
    }
}
