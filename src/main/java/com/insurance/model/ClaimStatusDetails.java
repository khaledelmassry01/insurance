package com.insurance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "claim_status_details")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "claimStatuses"})
public class ClaimStatusDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Integer statusId;

    @Column(name = "status_name", nullable = false)
    private String statusName;

    @OneToMany(mappedBy = "statusDetails")
    @JsonIgnoreProperties("statusDetails")
    private List<ClaimStatus> claimStatuses = new ArrayList<>();

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public List<ClaimStatus> getClaimStatuses() {
        return claimStatuses;
    }

    public void setClaimStatuses(List<ClaimStatus> claimStatuses) {
        this.claimStatuses = claimStatuses;
    }
}
