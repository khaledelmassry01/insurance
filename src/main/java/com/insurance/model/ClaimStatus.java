// ClaimStatus.java
package com.insurance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "claim_status")
@IdClass(ClaimStatusId.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "claim"})
public class ClaimStatus {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private ClaimStatusDetails statusDetails;

    @Id
    @Column(name = "status_date", nullable = false)
    private LocalDate statusDate;

    public ClaimStatus() {}

    public ClaimStatus(Claim claim, ClaimStatusDetails statusDetails, LocalDate statusDate) {
        this.claim = claim;
        this.statusDetails = statusDetails;
        this.statusDate = statusDate;
    }

    public Claim getClaim() {
        return claim;
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
    }

    public ClaimStatusDetails getStatusDetails() {
        return statusDetails;
    }

    public void setStatusDetails(ClaimStatusDetails statusDetails) {
        this.statusDetails = statusDetails;
    }

    public LocalDate getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(LocalDate statusDate) {
        this.statusDate = statusDate;
    }
    
    // Add transient methods to expose status ID and name for JSON serialization
    @Transient
    public Integer getStatusId() {
        return statusDetails != null ? statusDetails.getStatusId() : null;
    }
    
    @Transient
    public String getStatusName() {
        return statusDetails != null ? statusDetails.getStatusName() : null;
    }
}
