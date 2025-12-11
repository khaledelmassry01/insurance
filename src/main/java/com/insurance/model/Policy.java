package com.insurance.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "policies")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Integer policyId;

    @Column(name = "policy_type", nullable = false)
    private String policyType;

    @OneToMany(
        mappedBy = "policy",
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @JsonBackReference
    private List<InsuranceCoverage> insuranceCoverages = new ArrayList<>();

    // —————— Getters & Setters ——————

    public Integer getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Integer policyId) {
        this.policyId = policyId;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public List<InsuranceCoverage> getInsuranceCoverages() {
        return insuranceCoverages;
    }

    public void setInsuranceCoverages(List<InsuranceCoverage> insuranceCoverages) {
        this.insuranceCoverages = insuranceCoverages;
    }
}
