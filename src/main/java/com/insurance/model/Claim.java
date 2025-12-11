package com.insurance.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "claims")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "claim_id")
    private Integer claimId;

    @Column(name = "incident_description", nullable = false)
    private String incidentDescription;

    @Column(name = "amount_claimed", precision = 38, scale = 2, nullable = false)
    private BigDecimal amountClaimed;
    
    @Column(name = "claim_date")
    private LocalDate claimDate;
    
    @Column(name = "incident_date")
    private LocalDate incidentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policyholder_id", nullable = false)
    @JsonBackReference(value = "policyholder-claims")
    @JsonIgnoreProperties("claims")
    private Policyholder policyholder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_id", nullable = false)
    @JsonBackReference(value = "insurance-claims")
    @JsonIgnoreProperties({"claims", "payments"})
    private InsuranceCoverage insuranceCoverage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    @JsonBackReference(value = "agent-claims")
    private Agent agent;

    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnoreProperties({"claim", "statusDetails"})
    private List<ClaimStatus> claimStatuses = new ArrayList<>();
    
    /**
     * Transient field to store the insurance ID (policy ID) for easier access in the frontend
     * This prevents the need to navigate through the nested object structure
     */
    @Transient
    private Integer insuranceId;

    public Claim() {}

    // Getters and Setters
    
    public Integer getClaimId() {
        return claimId;
    }

    public void setClaimId(Integer claimId) {
        this.claimId = claimId;
    }

    public String getIncidentDescription() {
        return incidentDescription;
    }

    public void setIncidentDescription(String incidentDescription) {
        this.incidentDescription = incidentDescription;
    }

    public BigDecimal getAmountClaimed() {
        return amountClaimed;
    }

    public void setAmountClaimed(BigDecimal amountClaimed) {
        this.amountClaimed = amountClaimed;
    }
    
    public LocalDate getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(LocalDate claimDate) {
        this.claimDate = claimDate;
    }

    public LocalDate getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(LocalDate incidentDate) {
        this.incidentDate = incidentDate;
    }

    public Policyholder getPolicyholder() {
        return policyholder;
    }

    public void setPolicyholder(Policyholder policyholder) {
        this.policyholder = policyholder;
    }

    public InsuranceCoverage getInsuranceCoverage() {
        return insuranceCoverage;
    }

    public void setInsuranceCoverage(InsuranceCoverage insuranceCoverage) {
        this.insuranceCoverage = insuranceCoverage;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public List<ClaimStatus> getClaimStatuses() {
        return claimStatuses;
    }

    public void setClaimStatuses(List<ClaimStatus> claimStatuses) {
        this.claimStatuses = claimStatuses;
    }
    
    public Integer getInsuranceId() {
        return insuranceId;
    }
    
    public void setInsuranceId(Integer insuranceId) {
        this.insuranceId = insuranceId;
    }

    // equals and hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Claim)) return false;
        Claim that = (Claim) o;
        return claimId != null && claimId.equals(that.claimId);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
